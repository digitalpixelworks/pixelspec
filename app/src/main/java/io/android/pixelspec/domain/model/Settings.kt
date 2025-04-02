package io.android.pixelspec.domain.model

enum class ThemeMode {
    LIGHT, DARK, SYSTEM;

    companion object {
        fun fromString(value: String): ThemeMode {
            return entries.firstOrNull { it.name == value } ?: SYSTEM
        }
    }
}