package io.android.pixelspec.data.datasource

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

interface HardwareDataSource {
    fun getCpuInfo(): CpuInfo
    fun getBatteryInfo(): BatteryInfo
    fun getMemoryInfo(): MemoryInfo
    fun getStorageInfo(): StorageInfo
    fun getDeviceInfo(): DeviceInfo
    fun getThermalInfo(): ThermalInfo?
    fun getNetworkInfo(): NetworkInfo?
    fun getSensorInfo(): SensorInfo?
    fun getDisplayInfo(): DisplayInfo?
    fun getSecurityInfo(): SecurityInfo?
}