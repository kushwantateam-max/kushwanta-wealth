package com.example.domain.util

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.data.prefs.AppPreferences
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

sealed class GoogleSignInOutcome {
    data class Success(val displayName: String, val email: String) : GoogleSignInOutcome()
    data class NotConfigured(val message: String) : GoogleSignInOutcome()
    data object Cancelled : GoogleSignInOutcome()
    data class Error(val message: String) : GoogleSignInOutcome()
}

sealed class EmailAuthOutcome {
    data class Success(val displayName: String, val email: String, val isLocalFallback: Boolean = false) : EmailAuthOutcome()
    data class Error(val message: String) : EmailAuthOutcome()
}

sealed class PasswordResetOutcome {
    data class Sent(val message: String) : PasswordResetOutcome()
    data class Error(val message: String) : PasswordResetOutcome()
}

/**
 * Crash-proof Firebase Auth & Google Credential Manager sync bridge.
 * Runs 100% safely offline or when Firebase is not yet provisioned (no google-services.json),
 * and auto-hydrates Gmail displayName, email, and photoUrl once Firebase Auth is connected.
 */
object ProfileSyncManager {

    fun isFirebaseAvailable(context: Context): Boolean = runCatching {
        if (FirebaseApp.getApps(context).isNotEmpty()) {
            true
        } else {
            FirebaseApp.initializeApp(context) != null
        }
    }.getOrDefault(false)

    fun syncCurrentFirebaseUser(context: Context, prefs: AppPreferences): Boolean = runCatching {
        if (!isFirebaseAvailable(context)) return@runCatching false
        val user = FirebaseAuth.getInstance().currentUser ?: return@runCatching false
        prefs.saveGoogleSyncedProfile(
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString()
        )
        prefs.markGoogleAuthCompleted()
        true
    }.getOrDefault(false)

    suspend fun signInWithGoogle(context: Context, prefs: AppPreferences): GoogleSignInOutcome {
        if (!isFirebaseAvailable(context)) {
            return GoogleSignInOutcome.NotConfigured(
                "Google Sync is ready — link your Firebase project to sign in live."
            )
        }
        val webClientIdRes = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        val webClientId = if (webClientIdRes != 0) {
            runCatching { context.getString(webClientIdRes) }.getOrNull().orEmpty().trim()
        } else ""

        if (webClientId.isBlank()) {
            return GoogleSignInOutcome.NotConfigured(
                "Google Sign-In Client ID pending Firebase setup. Use Email or Guest mode locally."
            )
        }

        return withContext(Dispatchers.Main.immediate) {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCred = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
                    val authResultUser = signInFirebaseCredential(firebaseCred)
                    val name = authResultUser?.displayName
                        ?: googleCredential.displayName
                        ?: "Koshvanta Member"
                    val email = authResultUser?.email ?: googleCredential.id
                    val photo = authResultUser?.photoUrl?.toString()
                        ?: googleCredential.profilePictureUri?.toString()
                    prefs.saveGoogleSyncedProfile(
                        displayName = name,
                        email = email,
                        photoUrl = photo
                    )
                    prefs.markGoogleAuthCompleted()
                    GoogleSignInOutcome.Success(name, email)
                } else {
                    GoogleSignInOutcome.Error("Unsupported credential type returned.")
                }
            } catch (_: GetCredentialCancellationException) {
                GoogleSignInOutcome.Cancelled
            } catch (t: Throwable) {
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
            prefs.saveEmailSignedInProfile(displayName = null, email = cleanEmail)
            return EmailAuthOutcome.Success(
                displayName = prefs.profileDisplayName.value,
                email = cleanEmail,
                isLocalFallback = true
            )
        }
        return suspendCancellableCoroutine { cont ->
            runCatching {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(cleanEmail, password)
                    .addOnSuccessListener { res ->
                        val user = res.user
                        val name = user?.displayName
                        val resolvedEmail = user?.email ?: cleanEmail
                        prefs.saveEmailSignedInProfile(displayName = name, email = resolvedEmail)
                        if (cont.isActive) {
                            cont.resume(EmailAuthOutcome.Success(prefs.profileDisplayName.value, resolvedEmail))
                        }
                    }
                    .addOnFailureListener { err ->
                        if (cont.isActive) {
                            cont.resume(EmailAuthOutcome.Error(err.localizedMessage ?: "Sign in failed. Please check your credentials."))
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
            prefs.saveEmailSignedInProfile(displayName = cleanName, email = cleanEmail)
            return EmailAuthOutcome.Success(
                displayName = cleanName,
                email = cleanEmail,
                isLocalFallback = true
            )
        }
        return suspendCancellableCoroutine { cont ->
            runCatching {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(cleanEmail, password)
                    .addOnSuccessListener { res ->
                        val user = res.user
                        runCatching {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(cleanName)
                                .build()
                            user?.updateProfile(profileUpdates)
                        }
                        prefs.saveEmailSignedInProfile(displayName = cleanName, email = cleanEmail)
                        if (cont.isActive) {
                            cont.resume(EmailAuthOutcome.Success(cleanName, cleanEmail))
                        }
                    }
                    .addOnFailureListener { err ->
                        if (cont.isActive) {
                            cont.resume(EmailAuthOutcome.Error(err.localizedMessage ?: "Could not create account."))
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
            return PasswordResetOutcome.Sent(
                "Password reset is ready — link Firebase to send live reset emails to $cleanEmail."
            )
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
                            cont.resume(PasswordResetOutcome.Error(err.localizedMessage ?: "Unable to send reset email."))
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
        if (isFirebaseAvailable(context)) {
            withContext(Dispatchers.Main.immediate) {
                runCatching {
                    FirebaseAuth.getInstance().signOut()
                }
                withTimeoutOrNull(800L) {
                    runCatching {
                        CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
                    }
                }
            }
        }
        prefs.clearGoogleSyncState()
    }

    suspend fun deleteActiveFirebaseUser(context: Context) {
        if (!isFirebaseAvailable(context)) return
        withContext(Dispatchers.Main.immediate) {
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
            runCatching { FirebaseAuth.getInstance().signOut() }
            withTimeoutOrNull(800L) {
                runCatching {
                    CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
                }
            }
        }
    }

    private suspend fun signInFirebaseCredential(
        credential: com.google.firebase.auth.AuthCredential
    ) = suspendCancellableCoroutine { cont ->
        runCatching {
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener { res ->
                    if (cont.isActive) cont.resume(res.user)
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(null)
                }
        }.onFailure {
            if (cont.isActive) cont.resume(null)
        }
    }
}
