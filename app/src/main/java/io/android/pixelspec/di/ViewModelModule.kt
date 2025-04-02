package io.android.pixelspec.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.android.pixelspec.data.repository.AppRepositoryImpl
import io.android.pixelspec.data.repository.SettingsRepositoryImpl
import io.android.pixelspec.domain.repository.AppRepository
import io.android.pixelspec.domain.repository.HardwareRepository
import io.android.pixelspec.domain.repository.SettingsRepository
import io.android.pixelspec.domain.usecase.GetHardwareInfoUseCase

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideGetHardwareInfoUseCase(
        repository: HardwareRepository
    ): GetHardwareInfoUseCase = GetHardwareInfoUseCase(repository)

    @Provides
    fun provideAppRepository(
        impl: AppRepositoryImpl
    ): AppRepository = impl

    @Provides
    fun provideSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository = impl
}