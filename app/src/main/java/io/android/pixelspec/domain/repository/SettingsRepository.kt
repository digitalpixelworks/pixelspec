package io.android.pixelspec.domain.repository

import io.android.pixelspec.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun updateThemeMode(mode: ThemeMode)
}