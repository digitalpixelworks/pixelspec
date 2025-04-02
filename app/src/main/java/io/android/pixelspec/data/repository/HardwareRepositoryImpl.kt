package io.android.pixelspec.data.repository

import io.android.pixelspec.data.datasource.HardwareDataSource
import io.android.pixelspec.domain.model.HardwareInfo
import io.android.pixelspec.domain.repository.HardwareRepository
import javax.inject.Inject

class HardwareRepositoryImpl @Inject constructor(
    private val dataSource: HardwareDataSource
) : HardwareRepository {

    override fun getHardwareInfo(): HardwareInfo {
        return HardwareInfo(
            cpu = dataSource.getCpuInfo(),
            battery = dataSource.getBatteryInfo(),
            memory = dataSource.getMemoryInfo(),
            storage = dataSource.getStorageInfo(),
            device = dataSource.getDeviceInfo(),
            thermal = dataSource.getThermalInfo(),
            network = dataSource.getNetworkInfo(),
            sensors = dataSource.getSensorInfo(),
            display = dataSource.getDisplayInfo(),
            security = dataSource.getSecurityInfo()
        )
    }
}