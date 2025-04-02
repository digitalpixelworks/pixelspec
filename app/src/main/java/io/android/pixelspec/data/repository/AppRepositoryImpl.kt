package io.android.pixelspec.data.repository

import android.content.pm.PackageManager
import io.android.pixelspec.domain.model.AppInfo
import io.android.pixelspec.domain.repository.AppRepository
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val packageManager: PackageManager
) : AppRepository {

    override suspend fun getInstalledApps(): List<AppInfo> {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { app ->
                try {
                    AppInfo(
                        name = app.loadLabel(packageManager).toString(),
                        packageName = app.packageName,
                        version = packageManager.getPackageInfo(app.packageName, 0).versionName,
                        icon = app.loadIcon(packageManager)
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedBy { it.name }
    }
}