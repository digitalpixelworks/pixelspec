package io.android.pixelspec.presentation.model

import io.android.pixelspec.domain.model.BatteryInfo
import io.android.pixelspec.domain.model.CpuInfo
import io.android.pixelspec.domain.model.DeviceInfo
import io.android.pixelspec.domain.model.DisplayInfo
import io.android.pixelspec.domain.model.MemoryInfo
import io.android.pixelspec.domain.model.NetworkInfo
import io.android.pixelspec.domain.model.SecurityInfo
import io.android.pixelspec.domain.model.SensorInfo
import io.android.pixelspec.domain.model.StorageInfo
import io.android.pixelspec.domain.model.ThermalInfo

sealed class HardwareState {
    object Loading : HardwareState()
    data class Success(
        val cpuInfo: CpuInfo,
        val batteryInfo: BatteryInfo,
        val memoryInfo: MemoryInfo,
        val storageInfo: StorageInfo,
        val deviceInfo: DeviceInfo,
        val thermalInfo: ThermalInfo?,
        val networkInfo: NetworkInfo?,
        val sensorInfo: SensorInfo?,
        val displayInfo: DisplayInfo?,
        val securityInfo: SecurityInfo?
    ) : HardwareState()

    data class Error(
        val message: String, val previousData: Success? = null
    ) : HardwareState()
}