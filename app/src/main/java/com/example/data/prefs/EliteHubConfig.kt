package com.example.data.prefs

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Centralized configuration and safe external intent launcher for Kushwanta Elite Hub.
 * Strictly read-only URL constants with zero hardcoded third-party package coupling.
 */
object EliteHubConfig {
    const val OFFICIAL_WHATSAPP_CHANNEL_URL =
        "https://whatsapp.com/channel/0029Vb9CDf2Lo4hd9hFarY33"
    const val KUSHWANTA_VIP_COMMUNITY_URL =
        "https://chat.whatsapp.com/C916CTYtlPgDNXPAsO9YIJ"

    private const val TAG = "EliteHubConfig"

    /**
     * Validates an HTTPS community URL and launches it safely via Android ACTION_VIEW.
     * Falls back to a browsable browser intent using the exact same HTTPS URL if needed.
     * Never throws on missing WhatsApp/browser; invokes [onError] so UI can show a Snackbar.
     */
    fun launchExternalCommunityUrl(
        context: Context,
        rawUrl: String,
        onError: () -> Unit
    ) {
        val trimmedUrl = rawUrl.trim()
        val parsedUri = runCatching { Uri.parse(trimmedUrl) }.getOrNull()
        val isHttpsValid = parsedUri != null &&
            parsedUri.scheme.equals("https", ignoreCase = true) &&
            !parsedUri.host.isNullOrBlank()

        if (!isHttpsValid || parsedUri == null) {
            Log.w(TAG, "Blocked invalid or non-HTTPS community URL")
            onError()
            return
        }

        val addNewTaskFlag = context !is Activity

        val primaryIntent = Intent(Intent.ACTION_VIEW, parsedUri).apply {
            if (addNewTaskFlag) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        try {
            context.startActivity(primaryIntent)
            return
        } catch (_: ActivityNotFoundException) {
            // Fall through to explicit browsable browser fallback using the exact same HTTPS URL.
        } catch (t: Throwable) {
            Log.w(TAG, "Primary ACTION_VIEW launch failed (${t.javaClass.simpleName}); trying browser fallback")
        }

        val browserFallbackIntent = Intent(Intent.ACTION_VIEW, parsedUri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            if (addNewTaskFlag) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        try {
            context.startActivity(browserFallbackIntent)
        } catch (t: Throwable) {
            Log.w(TAG, "Browser fallback failed for community URL (${t.javaClass.simpleName})")
            onError()
        }
    }
}
