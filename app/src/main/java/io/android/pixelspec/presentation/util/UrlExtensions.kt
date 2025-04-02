package io.android.pixelspec.presentation.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import timber.log.Timber

fun Context.openUrl(url: String) {
    try {
        startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = url.toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
    } catch (e: Exception) {
        Timber.e("openUrl: Error opening URL: $url")
    }
}