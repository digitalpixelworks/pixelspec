package io.android.pixelspec.domain.model

// HardwareInfo.kt
data class HardwareInfo(
    val cpu: CpuInfo,
    val battery: BatteryInfo,
    val memory: MemoryInfo,
    val storage: StorageInfo,
    val device: DeviceInfo,
    val thermal: ThermalInfo? = null,
    val network: NetworkInfo? = null,
    val sensors: SensorInfo? = null,
    val display: DisplayInfo? = null,
    val security: SecurityInfo? = null
)

// CpuInfo.kt
data class CpuInfo(
    val name: String,
    val cores: Int,
    val maxFreq: String,
    val architecture: String,
    val currentFreqs: List<String> = emptyList(),
    val load: CpuLoad? = null,
    val temperature: Float? = null,
    val governor: String? = null
) {
    data class CpuLoad(
        val user: Float, val system: Float, val idle: Float, val totalUsage: Float
    )
}

// BatteryInfo.kt
data class BatteryInfo(
    val capacity: String,
    val temperature: Float,
    val voltage: Float,
    val health: String,
    val currentNow: Int = 0,
    val currentAvg: Int = 0,
    val chargeCounter: Int = 0,
    val isCharging: Boolean = false,
    val chargeSource: String = "UNPLUGGED",
    val chargeStatus: String = "UNKNOWN",
    val technology: String? = null,
    val healthMetrics: BatteryHealth? = null,
    val chargeSpeed: Int? = null
) {
    data class BatteryHealth(
        val cycleCount: Int, val designCapacity: Int,    // mAh
        val currentCapacity: Int,    // mAh
        val wearLevel: Float         // Percentage
    )
}

// MemoryInfo.kt
data class MemoryInfo(
    val totalRam: String,
    val availableRam: String,
    val totalSwap: String,
    val zramUsage: String? = null,
    val details: MemoryDetails? = null
) {
    data class MemoryDetails(
        val active: String,
        val inactive: String,
        val cached: String,
        val buffers: String,
        val swapCached: String
    )
}

// StorageInfo.kt
data class StorageInfo(
    val totalInternal: String,
    val availableInternal: String,
    val performance: StoragePerformance? = null,
    val health: StorageHealth? = null
) {
    data class StoragePerformance(
        val readSpeed: String,    // MB/s
        val writeSpeed: String     // MB/s
    )

    data class StorageHealth(
        val totalBytesWritten: Long, val lifespanPercentage: Float
    )
}

// DeviceInfo.kt
data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val product: String,
    val hardware: String,
    val androidVersion: String,
    val sdkVersion: String,
    val security: DeviceSecurity? = null,
    val rootStatus: RootStatus? = null
) {
    data class DeviceSecurity(
        val selinuxStatus: String, val verifiedBoot: String, val securityPatch: String
    )

    enum class RootStatus {
        NOT_ROOTED, ROOTED, UNKNOWN
    }
}

// ThermalInfo.kt
data class ThermalInfo(
    val zones: List<ThermalZone>, val throttlingStatus: String
) {
    data class ThermalZone(
        val name: String, val type: String, val temp: Float  // Celsius
    )
}

// NetworkInfo.kt
data class NetworkInfo(
    val connectionType: String, val signalStrength: Int,    // dBm
    val ipAddress: String, val dataUsage: DataUsage
) {
    data class DataUsage(
        val txBytes: Long, val rxBytes: Long
    )
}

// SensorInfo.kt
data class SensorInfo(
    val availableSensors: List<Sensor>, val significantMotion: Boolean
) {
    data class Sensor(
        val name: String, val vendor: String, val type: String
    )
}

// DisplayInfo.kt
data class DisplayInfo(
    val refreshRate: Int,    // Hz
    val brightness: Int,     // 0-255
    val hdrCapabilities: List<String>, val screenOnTime: Long   // Milliseconds
)

// SecurityInfo.kt
data class SecurityInfo(
    val bootloaderStatus: String, val googlePlayProtect: Boolean, val encryptionStatus: String
)