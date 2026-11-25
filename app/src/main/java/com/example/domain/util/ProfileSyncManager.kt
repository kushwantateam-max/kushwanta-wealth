package com.example.domain.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.data.prefs.AppPreferences
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

sealed class GoogleSignInOutcome {
    data class Success(val displayName: String, val email: String) : GoogleSignInOutcome()
    data object Cancelled : GoogleSignInOutcome()
    data class Error(val message: String) : GoogleSignInOutcome()
}

sealed class EmailAuthOutcome {
    data class Success(val displayName: String, val email: String) : EmailAuthOutcome()
    data class Error(val message: String) : EmailAuthOutcome()
}

sealed class PasswordResetOutcome {
    data class Sent(val message: String) : PasswordResetOutcome()
    data class Error(val message: String) : PasswordResetOutcome()
}

/**
 * Live Firebase Authentication & Jetpack Credential Manager Google Sign-In bridge.
 * Auto-hydrates Gmail displayName, email, and profile photoUrl across Profile Hub & Leaderboard.
 */
object ProfileSyncManager {

    private const val TAG = "ProfileSyncManager"

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    fun isFirebaseAvailable(context: Context): Boolean = ensureFirebaseInitialized(context) != null

    private fun ensureFirebaseInitialized(context: Context): FirebaseApp? {
        val appCtx = context.applicationContext ?: context
        return runCatching {
            FirebaseApp.getApps(appCtx).firstOrNull()
                ?: FirebaseApp.initializeApp(appCtx)
                ?: FirebaseOptions.fromResource(appCtx)?.let { options ->
                    FirebaseApp.initializeApp(appCtx, options)
                }
        }.onFailure { err ->
            Log.e(TAG, "Firebase initialization error", err)
        }.getOrNull()
    }

    private fun extractBestPhotoUrl(user: FirebaseUser?): String? {
        if (user == null) return null
        val raw = user.photoUrl?.toString()?.takeIf { it.isNotBlank() }
            ?: user.providerData.firstNotNullOfOrNull { info ->
                info.photoUrl?.toString()?.takeIf { it.isNotBlank() }
            }
        return upgradeGooglePhotoResolution(raw)
    }

    private fun upgradeGooglePhotoResolution(url: String?): String? {
        val clean = url?.trim()?.takeIf { it.isNotBlank() } ?: return null
        return clean
            .replace("=s96-c", "=s256-c")
            .replace("/s96-c/", "/s256-c/")
    }

    fun syncCurrentFirebaseUser(context: Context, prefs: AppPreferences): Boolean = runCatching {
        if (!isFirebaseAvailable(context)) return@runCatching false
        val user = FirebaseAuth.getInstance().currentUser ?: return@runCatching false
        val resolvedPhoto = extractBestPhotoUrl(user)
        val hasGoogleProvider = user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }
        if (hasGoogleProvider || resolvedPhoto != null) {
            prefs.saveGoogleSyncedProfile(
                displayName = user.displayName,
                email = user.email,
                photoUrl = resolvedPhoto
            )
            if (hasGoogleProvider) {
                prefs.markGoogleAuthCompleted()
            }
        }
        true
    }.getOrDefault(false)

    suspend fun signInWithGoogle(context: Context, prefs: AppPreferences): GoogleSignInOutcome {
        if (!isFirebaseAvailable(context)) {
            return GoogleSignInOutcome.Error(
                "Authentication service failed to initialize. Please restart the app and try again."
            )
        }
        val webClientIdRes = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        val webClientId = if (webClientIdRes != 0) {
            runCatching { context.getString(webClientIdRes) }.getOrNull().orEmpty().trim()
        } else {
            ""
        }

        if (webClientId.isBlank()) {
            return GoogleSignInOutcome.Error(
                "Google Sign-In client configuration missing from app/google-services.json."
            )
        }

        val activityContext: Context = context.findActivity() ?: context

        return withContext(Dispatchers.Main.immediate) {
            try {
                val credentialManager = CredentialManager.create(activityContext)
                val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                    serverClientId = webClientId
                ).build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = activityContext,
                    request = request
                )
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCred = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
                    val firebaseResult = signInFirebaseCredential(firebaseCred)
                    val authResultUser = firebaseResult.getOrElse { err ->
                        return@withContext GoogleSignInOutcome.Error(
                            formatFirebaseAuthError(err, "Google Sign-In failed with Firebase.")
                        )
                    }
                    val name = authResultUser.displayName?.takeIf { it.isNotBlank() }
                        ?: googleCredential.displayName?.takeIf { it.isNotBlank() }
                        ?: "Koshvanta Member"
                    val email = authResultUser.email?.takeIf { it.isNotBlank() }
                        ?: googleCredential.id
                    val photo = extractBestPhotoUrl(authResultUser)
                        ?: upgradeGooglePhotoResolution(googleCredential.profilePictureUri?.toString())
                    prefs.saveGoogleSyncedProfile(
                        displayName = name,
                        email = email,
                        photoUrl = photo
                    )
                    prefs.markGoogleAuthCompleted()
                    GoogleSignInOutcome.Success(name, email)
                } else {
                    GoogleSignInOutcome.Error("Unsupported credential type returned by Google.")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "Google Sign-In cancelled by user: ${e.message}")
                GoogleSignInOutcome.Cancelled
            } catch (e: NoCredentialException) {
                Log.w(TAG, "No Google credential available: ${e.message}", e)
                GoogleSignInOutcome.Error(
                    "No Google account found on this device. Add a Google account in device Settings and try again."
                )
            } catch (e: GetCredentialException) {
                Log.e(TAG, "CredentialManager error during Google Sign-In", e)
                GoogleSignInOutcome.Error(
                    e.localizedMessage ?: "Google Sign-In could not complete."
                )
            } catch (t: Throwable) {
                Log.e(TAG, "Unexpected error during Google Sign-In", t)
                GoogleSignInOutcome.Error(t.localizedMessage ?: "Google Sign-In could not complete.")
            }
        }
    }

    suspend fun signInWithEmail(
        context: Context,
        email: String,
        password: String,
        prefs: AppPreferences
    ): EmailAuthOutcome {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return EmailAuthOutcome.Error("Please enter a valid email address.")
        }
        if (password.length < 6) {
            return EmailAuthOutcome.Error("Password must be at least 6 characters.")
        }
        if (!isFirebaseAvailable(context)) {
            return EmailAuthOutcome.Error("Authentication service unavailable. Please check your connection and try again.")
        }
        return suspendCancellableCoroutine { cont ->
            runCatching {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(cleanEmail, password)
                    .addOnSuccessListener { res ->
                        val user = res.user
                        val name = user?.displayName
                        val resolvedEmail = user?.email ?: cleanEmail
                        val resolvedPhoto = extractBestPhotoUrl(user)
                        prefs.saveEmailSignedInProfile(
                            displayName = name,
                            email = resolvedEmail,
                            photoUrl = resolvedPhoto
                        )
                        if (cont.isActive) {
                            cont.resume(EmailAuthOutcome.Success(prefs.profileDisplayName.value, resolvedEmail))
                        }
                    }
                    .addOnFailureListener { err ->
                        if (cont.isActive) {
                            cont.resume(
                                EmailAuthOutcome.Error(
                                    formatFirebaseAuthError(err, "Sign in failed. Please check your email and password.")
                                )
                            )
                        }
                    }
            }.onFailure { err ->
                if (cont.isActive) {
                    cont.resume(EmailAuthOutcome.Error(err.localizedMessage ?: "Sign in unavailable right now."))
                }
            }
        }
    }

    suspend fun createAccountWithEmail(
        context: Context,
        fullName: String,
        email: String,
        password: String,
        prefs: AppPreferences
    ): EmailAuthOutcome {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim()
        if (cleanName.length < 2) {
            return EmailAuthOutcome.Error("Please enter your full name.")
        }
        if (cleanEmail.isBlank() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return EmailAuthOutcome.Error("Please enter a valid email address.")
        }
        if (password.length < 6) {
            return EmailAuthOutcome.Error("Password must be at least 6 characters.")
        }
        if (!isFirebaseAvailable(context)) {
            return EmailAuthOutcome.Error("Authentication service unavailable. Please check your connection and try again.")
        }
        return suspendCancellableCoroutine { cont ->
            runCatching {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(cleanEmail, password)
                    .addOnSuccessListener { res ->
                        val user = res.user
                        if (user != null) {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(cleanName)
                                .build()
                            user.updateProfile(profileUpdates)
                                .addOnCompleteListener {
                                    prefs.saveEmailSignedInProfile(displayName = cleanName, email = cleanEmail)
                                    if (cont.isActive) {
                                        cont.resume(EmailAuthOutcome.Success(cleanName, cleanEmail))
                                    }
                                }
                        } else {
                            prefs.saveEmailSignedInProfile(displayName = cleanName, email = cleanEmail)
                            if (cont.isActive) {
                                cont.resume(EmailAuthOutcome.Success(cleanName, cleanEmail))
                            }
                        }
                    }
                    .addOnFailureListener { err ->
                        if (cont.isActive) {
                            cont.resume(
                                EmailAuthOutcome.Error(
                                    formatFirebaseAuthError(err, "Could not create account. Please try again.")
                                )
                            )
                        }
                    }
            }.onFailure { err ->
                if (cont.isActive) {
                    cont.resume(EmailAuthOutcome.Error(err.localizedMessage ?: "Account creation unavailable right now."))
                }
            }
        }
    }

    suspend fun sendPasswordResetEmail(
        context: Context,
        email: String
    ): PasswordResetOutcome {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return PasswordResetOutcome.Error("Enter your email address first to reset your password.")
        }
        if (!isFirebaseAvailable(context)) {
            return PasswordResetOutcome.Error("Password reset unavailable. Please check your connection and try again.")
        }
        return suspendCancellableCoroutine { cont ->
            runCatching {
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(cleanEmail)
                    .addOnSuccessListener {
                        if (cont.isActive) {
                            cont.resume(PasswordResetOutcome.Sent("Password reset email sent to $cleanEmail."))
                        }
                    }
                    .addOnFailureListener { err ->
                        if (cont.isActive) {
                            cont.resume(
                                PasswordResetOutcome.Error(
                                    formatFirebaseAuthError(err, "Unable to send reset email.")
                                )
                            )
                        }
                    }
            }.onFailure { err ->
                if (cont.isActive) {
                    cont.resume(PasswordResetOutcome.Error(err.localizedMessage ?: "Password reset unavailable."))
                }
            }
        }
    }

    suspend fun signOutGoogle(context: Context, prefs: AppPreferences) {
        withContext(Dispatchers.Main.immediate + NonCancellable) {
            withTimeoutOrNull(800L) {
                runCatching {
                    CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
                }
            }
            if (isFirebaseAvailable(context)) {
                runCatching {
                    FirebaseAuth.getInstance().signOut()
                }
            }
        }
        prefs.clearGoogleSyncState()
    }

    suspend fun deleteActiveFirebaseUser(context: Context) {
        if (!isFirebaseAvailable(context)) return
        withContext(Dispatchers.Main.immediate + NonCancellable) {
            withTimeoutOrNull(1500L) {
                suspendCancellableCoroutine { cont ->
                    runCatching {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user == null) {
                            if (cont.isActive) cont.resume(Unit)
                        } else {
                            user.delete()
                                .addOnCompleteListener {
                                    if (cont.isActive) cont.resume(Unit)
                                }
                        }
                    }.onFailure {
                        if (cont.isActive) cont.resume(Unit)
                    }
                }
            }
            withTimeoutOrNull(800L) {
                runCatching {
                    CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
                }
            }
            runCatching { FirebaseAuth.getInstance().signOut() }
        }
    }

    private suspend fun signInFirebaseCredential(
        credential: AuthCredential
    ): Result<FirebaseUser> = suspendCancellableCoroutine { cont ->
        runCatching {
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener { res ->
                    val user = res.user
                    if (cont.isActive) {
                        if (user != null) {
                            cont.resume(Result.success(user))
                        } else {
                            cont.resume(Result.failure(IllegalStateException("Firebase sign-in returned no user.")))
                        }
                    }
                }
                .addOnFailureListener { err ->
                    Log.e(TAG, "Firebase signInWithCredential failed", err)
                    if (cont.isActive) cont.resume(Result.failure(err))
                }
        }.onFailure { err ->
            Log.e(TAG, "Firebase signInWithCredential exception", err)
            if (cont.isActive) cont.resume(Result.failure(err))
        }
    }

    private fun formatFirebaseAuthError(err: Throwable, fallback: String): String = when (err) {
        is FirebaseNetworkException -> "Network error. Please check your internet connection and try again."
        is FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 6 characters."
        is FirebaseAuthUserCollisionException -> "An account with this email already exists. Try signing in instead."
        is FirebaseAuthInvalidUserException -> "No account found with this email address."
        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password. Please try again."
        else -> err.localizedMessage?.takeIf { it.isNotBlank() } ?: fallback
    }
}
