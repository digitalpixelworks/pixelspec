package io.android.pixelspec

import android.app.Application
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber

@HiltAndroidApp
class PixelSpecApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        deferAdsInitialization()
    }

    private fun deferAdsInitialization() {
        appScope.launch {
            // Wait for main thread to be idle
            delay(3000)

            try {
                withTimeout(5000) {
                    MobileAds.initialize(this@PixelSpecApplication) {
                        Timber.d("MobileAds initialized successfully")
                    }
                    // Preload an ad request
                    withContext(Dispatchers.IO) {
                        AdRequest.Builder().build()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Ads initialization failed")
            }
        }
    }

    override fun onTerminate() {
        appScope.cancel()
        super.onTerminate()
    }
}