package com.example.ads

/**
 * Centralized monetization configuration for Kushwanta.
 * Switch IS_TEST_MODE to false and replace these official test IDs with your live
 * Google AdMob and Unity Ads production IDs before Play Store release.
 */
object AdConfig {
    const val IS_TEST_MODE = true

    // Google AdMob (Primary)
    const val ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/9214589741"
    const val ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    const val ADMOB_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"

    // Unity Ads (Smart Fallback)
    const val UNITY_GAME_ID = "14851"
    const val UNITY_BANNER_PLACEMENT = "Banner_Android"
    const val UNITY_INTERSTITIAL_PLACEMENT = "Interstitial_Android"
    const val UNITY_REWARDED_PLACEMENT = "Rewarded_Android"

    // Public Unity test Game ID "14851" only configures "rewardedVideo" on Unity's backend
    // (interstitial and banner placements are not provisioned on "14851").
    // Switching UNITY_GAME_ID to your Unity Dashboard project ID automatically enables
    // Banner_Android, Interstitial_Android, and Rewarded_Android for all formats.
    internal fun isUnityBannerSupported(): Boolean =
        !(IS_TEST_MODE && UNITY_GAME_ID == "14851")

    internal fun isUnityInterstitialSupported(): Boolean =
        !(IS_TEST_MODE && UNITY_GAME_ID == "14851")

    internal fun resolvedUnityBannerPlacement(): String =
        if (IS_TEST_MODE && UNITY_GAME_ID == "14851") "banner" else UNITY_BANNER_PLACEMENT

    internal fun resolvedUnityInterstitialPlacement(): String =
        if (IS_TEST_MODE && UNITY_GAME_ID == "14851") "video" else UNITY_INTERSTITIAL_PLACEMENT

    internal fun resolvedUnityRewardedPlacement(): String =
        if (IS_TEST_MODE && UNITY_GAME_ID == "14851") "rewardedVideo" else UNITY_REWARDED_PLACEMENT
}
