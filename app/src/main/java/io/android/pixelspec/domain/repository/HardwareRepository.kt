package io.android.pixelspec.domain.repository

import io.android.pixelspec.domain.model.HardwareInfo

interface HardwareRepository {
    fun getHardwareInfo(): HardwareInfo
}