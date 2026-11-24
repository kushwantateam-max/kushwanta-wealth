package com.example.domain.util

import android.content.Context
import android.content.ContextWrapper
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricHelper {

    private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
        is FragmentActivity -> this
        is ContextWrapper -> baseContext.findFragmentActivity()
        else -> null
    }

    fun isBiometricAvailable(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                false
            } else {
                val manager = context.getSystemService(BiometricManager::class.java) ?: return false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val authenticators =
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.BIOMETRIC_WEAK
                    manager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
                } else {
                    @Suppress("DEPRECATION")
                    manager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                }
            }
        } catch (_: Throwable) {
            false
        }
    }

    fun authenticate(
        context: Context,
        title: String = "Unlock Koshvanta",
        subtitle: String = "Verify your identity to access your financial ledger",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                onError("Biometric prompt requires Android 9+ — use your 4-Digit Master PIN.")
                return
            }

            if (!isBiometricAvailable(context)) {
                onError("No biometrics enrolled on this device — use your 4-Digit Master PIN.")
                return
            }

            val hostContext: Context = try {
                context.findFragmentActivity() ?: context
            } catch (_: Throwable) {
                context
            }

            val executor = ContextCompat.getMainExecutor(hostContext)
            val cancellationSignal = CancellationSignal()

            val prompt = BiometricPrompt.Builder(hostContext)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButton("Use PIN", executor) { _, _ ->
                    try {
                        onError("Enter your 4-Digit Master PIN.")
                    } catch (_: Throwable) {
                        // Silently keep PIN pad responsive
                    }
                }
                .build()

            prompt.authenticate(
                cancellationSignal,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        try {
                            super.onAuthenticationSucceeded(result)
                            onSuccess()
                        } catch (_: Throwable) {
                            // Silently ignore unexpected callback errors
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        try {
                            super.onAuthenticationError(errorCode, errString)
                            val msg = errString?.toString()?.takeIf { it.isNotBlank() }
                                ?: "Biometric canceled — use your 4-Digit Master PIN."
                            onError(msg)
                        } catch (_: Throwable) {
                            // Silently ignore unexpected callback errors
                        }
                    }

                    override fun onAuthenticationFailed() {
                        try {
                            super.onAuthenticationFailed()
                            onError("Biometric did not match. Try again or enter 4-Digit PIN.")
                        } catch (_: Throwable) {
                            // Silently ignore unexpected callback errors
                        }
                    }
                }
            )
        } catch (_: Throwable) {
            // Silently ignore any BiometricManager / BiometricPrompt hardware or OS exception
            // so the 4-digit PIN pad remains open and responsive.
        }
    }
}
