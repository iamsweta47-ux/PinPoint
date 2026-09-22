package com.example.ads

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Advertising policy friendly abstraction.
 * Decouples advertising SDKs (e.g. Google AdMob) from core healthcare search logic.
 * Follows strict Google Play advertising policies:
 * - Never obscures navigation or hospital action buttons
 * - Clearly distinguished from functional medical content
 * - Interstitials only triggered on natural transitions (e.g. returning from details after deliberate search)
 */
interface AdManager {
    val isAdsEnabled: Boolean
    val bannerAdUnitId: String
    val interstitialAdUnitId: String

    fun initialize(context: Context)
    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit)
    fun isInterstitialReady(): Boolean
}

class DefaultAdManager : AdManager {
    // Configurable via build or remote config; default placeholder follows Google test/placeholder conventions
    override val isAdsEnabled: Boolean = true
    override val bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111" // Standard test ad unit ID placeholder
    override val interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712"

    private val _isReady = MutableStateFlow(true)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    override fun initialize(context: Context) {
        // Ready for production SDK initialization (e.g., MobileAds.initialize(context))
    }

    override fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        // In clean production mode without third-party ad binaries bundled,
        // execute callback directly without blocking user navigation.
        onAdDismissed()
    }

    override fun isInterstitialReady(): Boolean = true
}
