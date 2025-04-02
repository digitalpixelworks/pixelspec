package io.android.pixelspec.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.android.pixelspec.data.repository.HardwareRepositoryImpl
import io.android.pixelspec.domain.repository.HardwareRepository
import javax.inject.Singleton

// Top-level extension property for DataStore. This must be declared at the top level.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHardwareRepository(
        dataSource: io.android.pixelspec.data.datasource.HardwareDataSource
    ): HardwareRepository = HardwareRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }
}
