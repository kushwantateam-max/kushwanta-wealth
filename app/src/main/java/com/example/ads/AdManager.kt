package com.example.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.ref.WeakReference

/**
 * Centralized monetization controller for Kushwanta.
 *
 * Waterfall Priority:
 * 1. Primary: Google AdMob (Anchored Adaptive Banner, InterstitialAd, RewardedAd)
 * 2. Smart Fallback: Unity Ads (BannerView, Interstitial_Android, Rewarded_Android)
 *
 * Play Store Anti-Spam & UX Guardrails:
 * - Zero launch ads (15-second startup grace window).
 * - Strict 90-second global cooldown between interstitials.
 * - Mind Arena interstitials checked at most once every 3 completed game rounds.
 * - Rewarded benefits granted strictly on verified ad completion.
 */
object AdManager {
    private const val TAG = "KushwantaAdManager"

    const val MIN_INTERSTITIAL_COOLDOWN_MS = 90_000L
    const val ROUNDS_PER_INTERSTITIAL = 3
    private const val LAUNCH_GRACE_MS = 15_000L

    private val mainHandler = Handler(Looper.getMainLooper())
    private val appSessionStartedAtMs: Long = System.currentTimeMillis()

    private var appContext: Context? = null
    private var currentActivityRef: WeakReference<Activity>? = null

    private var isAdMobInitialized = false
    private var isUnityInitialized = false
    private var initAttempted = false

    private var admobInterstitial: InterstitialAd? = null
    private var isAdMobInterstitialLoading = false
    private var unityInterstitialLoaded = false
    private var isUnityInterstitialLoading = false
    private var unityInterstitialConfigInvalid = false

    private var admobRewarded: RewardedAd? = null
    private var isAdMobRewardedLoading = false
    private var unityRewardedLoaded = false
    private var isUnityRewardedLoading = false
    private var unityRewardedConfigInvalid = false

    private var lastInterstitialShownAtMs: Long = 0L
    private var completedGameRoundsSinceInterstitial: Int = 0
    private var lastInterstitialPlacement: String? = null
    private var totalRewardedCompletions: Int = 0

    private val _bannerLoadedStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val bannerLoadedStates: StateFlow<Map<String, Boolean>> = _bannerLoadedStates.asStateFlow()

    fun init(activity: Activity) {
        currentActivityRef = WeakReference(activity)
        val applicationContext = activity.applicationContext
        appContext = applicationContext

        if (initAttempted) return
        initAttempted = true

        // 1. Safe Google AdMob Initialization
        try {
            MobileAds.initialize(applicationContext) {
                isAdMobInitialized = true
                runOnMain {
                    preloadAdMobInterstitial()
                    preloadAdMobRewarded()
                }
            }
        } catch (t: Throwable) {
            Log.w(TAG, "AdMob initialization skipped safely: ${t.message}")
        }

        // 2. Safe Unity Ads Fallback Initialization
        try {
            UnityAds.initialize(
                applicationContext,
                AdConfig.UNITY_GAME_ID,
                AdConfig.IS_TEST_MODE,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        isUnityInitialized = true
                        runOnMain {
                            preloadUnityInterstitial()
                            preloadUnityRewarded()
                        }
                    }

                    override fun onInitializationFailed(
                        error: UnityAds.UnityAdsInitializationError?,
                        message: String?
                    ) {
                        Log.w(TAG, "Unity Ads init failed ($error): $message")
                    }
                }
            )
        } catch (t: Throwable) {
            Log.w(TAG, "Unity Ads initialization skipped safely: ${t.message}")
        }
    }

    fun setCurrentActivity(activity: Activity?) {
        currentActivityRef = activity?.let { WeakReference(it) }
    }

    fun setBannerLoaded(placementId: String, loaded: Boolean) {
        val current = _bannerLoadedStates.value
        if (current[placementId] == loaded) return
        _bannerLoadedStates.value = current + (placementId to loaded)
    }

    fun isInterstitialEligible(nowMs: Long = System.currentTimeMillis()): Boolean {
        if ((nowMs - appSessionStartedAtMs) < LAUNCH_GRACE_MS) {
            return false
        }
        if (lastInterstitialShownAtMs > 0L && (nowMs - lastInterstitialShownAtMs) < MIN_INTERSTITIAL_COOLDOWN_MS) {
            return false
        }
        return true
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }

    private fun getActiveActivity(): Activity? {
        val act = currentActivityRef?.get() ?: return null
        if (act.isFinishing || act.isDestroyed) return null
        return act
    }

    // -------------------------------------------------------------------------
    // Preloading (Primary AdMob + Secondary Unity Ads)
    // -------------------------------------------------------------------------

    private fun preloadAdMobInterstitial() {
        val ctx = appContext ?: return
        if (!isAdMobInitialized || admobInterstitial != null || isAdMobInterstitialLoading) return
        isAdMobInterstitialLoading = true
        try {
            InterstitialAd.load(
                ctx,
                AdConfig.ADMOB_INTERSTITIAL_ID,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        isAdMobInterstitialLoading = false
                        admobInterstitial = ad
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isAdMobInterstitialLoading = false
                        admobInterstitial = null
                        Log.w(TAG, "AdMob Interstitial load failed: ${error.message}")
                        preloadUnityInterstitial()
                    }
                }
            )
        } catch (t: Throwable) {
            isAdMobInterstitialLoading = false
            Log.w(TAG, "AdMob Interstitial preload error: ${t.message}")
        }
    }

    private fun preloadUnityInterstitial() {
        if (!AdConfig.isUnityInterstitialSupported()) return
        if (!isUnityInitialized || unityInterstitialLoaded || isUnityInterstitialLoading || unityInterstitialConfigInvalid) return
        isUnityInterstitialLoading = true
        val placementId = AdConfig.resolvedUnityInterstitialPlacement()
        try {
            UnityAds.load(
                placementId,
                object : IUnityAdsLoadListener {
                    override fun onUnityAdsAdLoaded(placementId: String?) {
                        isUnityInterstitialLoading = false
                        unityInterstitialLoaded = true
                    }

                    override fun onUnityAdsFailedToLoad(
                        placementId: String?,
                        error: UnityAds.UnityAdsLoadError?,
                        message: String?
                    ) {
                        isUnityInterstitialLoading = false
                        unityInterstitialLoaded = false
                        if (error == UnityAds.UnityAdsLoadError.INVALID_ARGUMENT) {
                            unityInterstitialConfigInvalid = true
                        }
                        Log.w(TAG, "Unity Interstitial load failed ($error): $message")
                    }
                }
            )
        } catch (t: Throwable) {
            isUnityInterstitialLoading = false
            Log.w(TAG, "Unity Interstitial preload error: ${t.message}")
        }
    }

    private fun preloadAdMobRewarded() {
        val ctx = appContext ?: return
        if (!isAdMobInitialized || admobRewarded != null || isAdMobRewardedLoading) return
        isAdMobRewardedLoading = true
        try {
            RewardedAd.load(
                ctx,
                AdConfig.ADMOB_REWARDED_ID,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        isAdMobRewardedLoading = false
                        admobRewarded = ad
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isAdMobRewardedLoading = false
                        admobRewarded = null
                        Log.w(TAG, "AdMob Rewarded load failed: ${error.message}")
                        preloadUnityRewarded()
                    }
                }
            )
        } catch (t: Throwable) {
            isAdMobRewardedLoading = false
            Log.w(TAG, "AdMob Rewarded preload error: ${t.message}")
        }
    }

    private fun preloadUnityRewarded() {
        if (!isUnityInitialized || unityRewardedLoaded || isUnityRewardedLoading || unityRewardedConfigInvalid) return
        isUnityRewardedLoading = true
        val placementId = AdConfig.resolvedUnityRewardedPlacement()
        try {
            UnityAds.load(
                placementId,
                object : IUnityAdsLoadListener {
                    override fun onUnityAdsAdLoaded(placementId: String?) {
                        isUnityRewardedLoading = false
                        unityRewardedLoaded = true
                    }

                    override fun onUnityAdsFailedToLoad(
                        placementId: String?,
                        error: UnityAds.UnityAdsLoadError?,
                        message: String?
                    ) {
                        isUnityRewardedLoading = false
                        unityRewardedLoaded = false
                        if (error == UnityAds.UnityAdsLoadError.INVALID_ARGUMENT) {
                            unityRewardedConfigInvalid = true
                        }
                        Log.w(TAG, "Unity Rewarded load failed ($error): $message")
                    }
                }
            )
        } catch (t: Throwable) {
            isUnityRewardedLoading = false
            Log.w(TAG, "Unity Rewarded preload error: ${t.message}")
        }
    }

    // -------------------------------------------------------------------------
    // Interstitial Waterfall (AdMob Primary -> Unity Ads Fallback)
    // -------------------------------------------------------------------------

    fun showInterstitialAd(
        placement: String = "hub_transition",
        onComplete: () -> Unit
    ) {
        val nowMs = System.currentTimeMillis()
        if (!isInterstitialEligible(nowMs)) {
            onComplete()
            return
        }

        val activity = getActiveActivity()
        if (activity == null) {
            onComplete()
            return
        }

        var completed = false
        fun finishOnce() {
            if (!completed) {
                completed = true
                runOnMain { onComplete() }
            }
        }

        val primaryAd = admobInterstitial
        if (primaryAd != null) {
            admobInterstitial = null
            lastInterstitialShownAtMs = nowMs
            completedGameRoundsSinceInterstitial = 0
            lastInterstitialPlacement = placement
            try {
                primaryAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        preloadAdMobInterstitial()
                        finishOnce()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.w(TAG, "AdMob Interstitial failed to show: ${adError.message}")
                        preloadAdMobInterstitial()
                        tryShowUnityInterstitial(activity, ::finishOnce)
                    }
                }
                primaryAd.show(activity)
                return
            } catch (t: Throwable) {
                Log.w(TAG, "AdMob Interstitial show exception: ${t.message}")
                preloadAdMobInterstitial()
            }
        } else {
            preloadAdMobInterstitial()
        }

        if (unityInterstitialLoaded) {
            lastInterstitialShownAtMs = nowMs
            completedGameRoundsSinceInterstitial = 0
            lastInterstitialPlacement = placement
            tryShowUnityInterstitial(activity, ::finishOnce)
        } else {
            preloadUnityInterstitial()
            finishOnce()
        }
    }

    private fun tryShowUnityInterstitial(
        activity: Activity,
        finishOnce: () -> Unit
    ) {
        if (!AdConfig.isUnityInterstitialSupported() || !unityInterstitialLoaded) {
            preloadUnityInterstitial()
            finishOnce()
            return
        }
        unityInterstitialLoaded = false
        try {
            UnityAds.show(
                activity,
                AdConfig.resolvedUnityInterstitialPlacement(),
                UnityAdsShowOptions(),
                object : IUnityAdsShowListener {
                    override fun onUnityAdsShowFailure(
                        placementId: String?,
                        error: UnityAds.UnityAdsShowError?,
                        message: String?
                    ) {
                        Log.w(TAG, "Unity Interstitial show failure ($error): $message")
                        preloadUnityInterstitial()
                        finishOnce()
                    }

                    override fun onUnityAdsShowStart(placementId: String?) = Unit

                    override fun onUnityAdsShowClick(placementId: String?) = Unit

                    override fun onUnityAdsShowComplete(
                        placementId: String?,
                        state: UnityAds.UnityAdsShowCompletionState?
                    ) {
                        preloadUnityInterstitial()
                        finishOnce()
                    }
                }
            )
        } catch (t: Throwable) {
            Log.w(TAG, "Unity Interstitial show exception: ${t.message}")
            preloadUnityInterstitial()
            finishOnce()
        }
    }

    fun recordGameRoundCompleted(
        placement: String = "arena_round_complete",
        onComplete: () -> Unit = {}
    ) {
        completedGameRoundsSinceInterstitial += 1
        val nowMs = System.currentTimeMillis()
        if (completedGameRoundsSinceInterstitial >= ROUNDS_PER_INTERSTITIAL && isInterstitialEligible(nowMs)) {
            showInterstitialAd(placement = placement, onComplete = onComplete)
        } else {
            onComplete()
        }
    }

    // -------------------------------------------------------------------------
    // Rewarded Waterfall (AdMob Primary -> Unity Ads Fallback)
    // Grants onRewardEarned() strictly upon completed video watch.
    // -------------------------------------------------------------------------

    fun showRewardedAd(
        rewardType: String,
        onRewardEarned: () -> Unit
    ) {
        val activity = getActiveActivity()
        if (activity == null) {
            preloadAdMobRewarded()
            preloadUnityRewarded()
            return
        }

        var rewardDelivered = false
        fun deliverRewardOnce() {
            if (!rewardDelivered) {
                rewardDelivered = true
                totalRewardedCompletions += 1
                runOnMain { onRewardEarned() }
            }
        }

        val primaryRewarded = admobRewarded
        if (primaryRewarded != null) {
            admobRewarded = null
            try {
                var earnedFromAdMob = false
                primaryRewarded.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        preloadAdMobRewarded()
                        if (earnedFromAdMob) {
                            deliverRewardOnce()
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.w(TAG, "AdMob Rewarded show failed for $rewardType: ${adError.message}")
                        preloadAdMobRewarded()
                        tryShowUnityRewarded(activity, rewardType, ::deliverRewardOnce)
                    }
                }
                primaryRewarded.show(activity) {
                    earnedFromAdMob = true
                    deliverRewardOnce()
                }
                return
            } catch (t: Throwable) {
                Log.w(TAG, "AdMob Rewarded show exception: ${t.message}")
                preloadAdMobRewarded()
            }
        } else {
            preloadAdMobRewarded()
        }

        tryShowUnityRewarded(activity, rewardType, ::deliverRewardOnce)
    }

    private fun tryShowUnityRewarded(
        activity: Activity,
        rewardType: String,
        deliverRewardOnce: () -> Unit
    ) {
        if (!unityRewardedLoaded) {
            preloadUnityRewarded()
            runOnMain {
                Toast.makeText(
                    activity,
                    "Loading reward video, please try again shortly.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        unityRewardedLoaded = false
        try {
            UnityAds.show(
                activity,
                AdConfig.resolvedUnityRewardedPlacement(),
                UnityAdsShowOptions(),
                object : IUnityAdsShowListener {
                    override fun onUnityAdsShowFailure(
                        placementId: String?,
                        error: UnityAds.UnityAdsShowError?,
                        message: String?
                    ) {
                        Log.w(TAG, "Unity Rewarded show failure for $rewardType ($error): $message")
                        preloadUnityRewarded()
                    }

                    override fun onUnityAdsShowStart(placementId: String?) = Unit

                    override fun onUnityAdsShowClick(placementId: String?) = Unit

                    override fun onUnityAdsShowComplete(
                        placementId: String?,
                        state: UnityAds.UnityAdsShowCompletionState?
                    ) {
                        preloadUnityRewarded()
                        if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                            deliverRewardOnce()
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            Log.w(TAG, "Unity Rewarded show exception: ${t.message}")
            preloadUnityRewarded()
        }
    }
}

private enum class ActiveBannerNetwork {
    ADMOB,
    UNITY,
    NONE
}

private tailrec fun Context.findHostActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findHostActivity()
    else -> null
}

/**
 * Dynamic Anchored Adaptive Banner container for primary hubs (Home Dashboard & Finance Tools Hub).
 *
 * Waterfall:
 * 1. Primary: Google AdMob Anchored Adaptive Banner (`AdConfig.ADMOB_BANNER_ID`)
 * 2. Fallback: Unity Ads BannerView (`AdConfig.UNITY_BANNER_PLACEMENT`)
 * 3. Collapses cleanly to 0.dp when neither network is loaded or device is offline.
 */
@Composable
fun BannerAdSlot(
    placementId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hostActivity = remember(context) { context.findHostActivity() }

    var activeNetwork by remember(placementId) { mutableStateOf(ActiveBannerNetwork.ADMOB) }
    var isBannerLoaded by remember(placementId) { mutableStateOf(false) }

    DisposableEffect(placementId) {
        onDispose {
            AdManager.setBannerLoaded(placementId, false)
        }
    }

    if (hostActivity == null || activeNetwork == ActiveBannerNetwork.NONE) {
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isBannerLoaded) {
                    Modifier.wrapContentHeight()
                } else {
                    Modifier
                        .height(0.dp)
                        .alpha(0f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        val bannerWidthDp = maxWidth.value.toInt().coerceAtLeast(320)

        when (activeNetwork) {
            ActiveBannerNetwork.ADMOB -> {
                val adView = remember(placementId, bannerWidthDp) {
                    try {
                        AdView(hostActivity).apply {
                            val adaptiveSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                                hostActivity,
                                bannerWidthDp
                            )
                            setAdSize(adaptiveSize)
                            adUnitId = AdConfig.ADMOB_BANNER_ID
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    isBannerLoaded = true
                                    AdManager.setBannerLoaded(placementId, true)
                                }

                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    isBannerLoaded = false
                                    AdManager.setBannerLoaded(placementId, false)
                                    activeNetwork = if (AdConfig.isUnityBannerSupported()) {
                                        ActiveBannerNetwork.UNITY
                                    } else {
                                        ActiveBannerNetwork.NONE
                                    }
                                }
                            }
                            loadAd(AdRequest.Builder().build())
                        }
                    } catch (_: Throwable) {
                        isBannerLoaded = false
                        activeNetwork = if (AdConfig.isUnityBannerSupported()) {
                            ActiveBannerNetwork.UNITY
                        } else {
                            ActiveBannerNetwork.NONE
                        }
                        null
                    }
                }

                if (adView != null) {
                    DisposableEffect(adView) {
                        onDispose {
                            try {
                                adView.destroy()
                            } catch (_: Throwable) {
                            }
                        }
                    }
                    AndroidView(
                        factory = { adView },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ActiveBannerNetwork.UNITY -> {
                val unityBanner = remember(placementId) {
                    try {
                        BannerView(
                            hostActivity,
                            AdConfig.resolvedUnityBannerPlacement(),
                            UnityBannerSize(320, 50)
                        ).apply {
                            listener = object : BannerView.IListener {
                                override fun onBannerLoaded(bannerAdView: BannerView?) {
                                    isBannerLoaded = true
                                    AdManager.setBannerLoaded(placementId, true)
                                }

                                override fun onBannerShown(bannerAdView: BannerView?) = Unit

                                override fun onBannerClick(bannerAdView: BannerView?) = Unit

                                override fun onBannerFailedToLoad(
                                    bannerAdView: BannerView?,
                                    errorInfo: BannerErrorInfo?
                                ) {
                                    isBannerLoaded = false
                                    AdManager.setBannerLoaded(placementId, false)
                                    activeNetwork = ActiveBannerNetwork.NONE
                                }

                                override fun onBannerLeftApplication(bannerAdView: BannerView?) = Unit
                            }
                            load()
                        }
                    } catch (_: Throwable) {
                        isBannerLoaded = false
                        activeNetwork = ActiveBannerNetwork.NONE
                        null
                    }
                }

                if (unityBanner != null) {
                    DisposableEffect(unityBanner) {
                        onDispose {
                            try {
                                unityBanner.destroy()
                            } catch (_: Throwable) {
                            }
                        }
                    }
                    AndroidView(
                        factory = { unityBanner },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ActiveBannerNetwork.NONE -> Unit
        }
    }
}
