package io.android.pixelspec.domain.repository

import io.android.pixelspec.domain.model.AppInfo

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
}