package io.android.pixelspec.domain.usecase

import io.android.pixelspec.domain.model.ThemeMode
import io.android.pixelspec.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<ThemeMode> = repository.themeMode
}