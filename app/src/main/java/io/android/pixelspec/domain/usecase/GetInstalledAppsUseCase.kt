package io.android.pixelspec.domain.usecase

import io.android.pixelspec.domain.model.AppInfo
import io.android.pixelspec.domain.repository.AppRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(): List<AppInfo> = repository.getInstalledApps()
}