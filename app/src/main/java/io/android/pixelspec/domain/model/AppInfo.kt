package io.android.pixelspec.domain.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String, val packageName: String, val version: String?, val icon: Drawable
)