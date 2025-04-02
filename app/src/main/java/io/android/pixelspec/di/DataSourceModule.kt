package io.android.pixelspec.di

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.DISPLAY_SERVICE
import android.content.Context.POWER_SERVICE
import android.content.Context.SENSOR_SERVICE
import android.net.ConnectivityManager
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.os.PowerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.android.pixelspec.data.datasource.HardwareDataSource
import io.android.pixelspec.data.datasource.SystemHardwareDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager {
        return context.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    @Provides
    @Singleton
    fun provideDisplayManager(@ApplicationContext context: Context): DisplayManager {
        return context.getSystemService(DISPLAY_SERVICE) as DisplayManager
    }

    @Provides
    @Singleton
    fun providePowerManager(@ApplicationContext context: Context): PowerManager {
        return context.getSystemService(POWER_SERVICE) as PowerManager
    }

    @Provides
    @Singleton
    fun provideHardwareDataSource(
        @ApplicationContext context: Context,
        connectivityManager: ConnectivityManager,
        sensorManager: SensorManager,
        displayManager: DisplayManager,
        powerManager: PowerManager
    ): HardwareDataSource {
        return SystemHardwareDataSource(
            context, connectivityManager, sensorManager, displayManager, powerManager
        )
    }
}
