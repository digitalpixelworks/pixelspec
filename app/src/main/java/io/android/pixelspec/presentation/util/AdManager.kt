package io.android.pixelspec.presentation.util

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import io.android.pixelspec.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber

// Updated AdManager.kt
class AdManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false
    private var initializationAttempted = false
    private val initializationLock = Mutex()

    private val adUnitId = BuildConfig.ADMOB_BANNER_ID

    suspend fun ensureInitialized(): Boolean {
        if (isInitialized) return true
        if (initializationAttempted) return false // Don't retry if failed before

        return initializationLock.withLock {
            if (isInitialized) return@withLock true

            try {
                withTimeout(3000) {
                    MobileAds.initialize(context)
                    isInitialized = true
                    true
                }
            } catch (e: Exception) {
                initializationAttempted = true
                Timber.e(e, "AdManager initialization failed")
                false
            }
        }
    }

    fun loadBannerAd(adView: AdView, onLoaded: (() -> Unit)? = null) {
        scope.launch {
            val initialized = ensureInitialized()
            if (!initialized) return@launch

            withContext(Dispatchers.Main) {
                try {
                    adView.adUnitId = adUnitId
                    adView.setAdSize(
                        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                            context, 360
                        )
                    )
                    adView.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            onLoaded?.invoke()
                        }
                    }
                    adView.loadAd(AdRequest.Builder().build())
                } catch (e: Exception) {
                    Timber.e(e, "Ad loading failed")
                }
            }
        }
    }

    fun destroy() {
        scope.cancel()
    }
}