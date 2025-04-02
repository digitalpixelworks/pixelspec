package io.android.pixelspec.presentation.navigation

object Destinations {
    const val HOME = "home"
    const val APPS = "apps"
    const val SETTINGS = "settings"
    const val ABOUT = "about"

    // Add nested navigation graphs if needed
    object Settings {
        const val ROOT = "settings_root"
        const val THEME = "theme_settings"
    }
}