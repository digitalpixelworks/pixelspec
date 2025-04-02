package io.android.pixelspec.data.datasource

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.view.Display
import dagger.hilt.android.qualifiers.ApplicationContext
import io.android.pixelspec.domain.model.*
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import javax.inject.Inject

@Suppress("DEPRECATION")
class SystemHardwareDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val connectivityManager: ConnectivityManager,
    private val sensorManager: SensorManager,
    private val displayManager: DisplayManager,
    private val powerManager: PowerManager
) : HardwareDataSource {

    // CPU Implementation
    override fun getCpuInfo(): CpuInfo {
        return try {
            val cpuInfoMap = readProcCpuInfo()
            val coreFreqs = (0 until Runtime.getRuntime().availableProcessors()).map { core ->
                readCpuCoreFrequency(core)
            }
            CpuInfo(
                name = cpuInfoMap["model name"] ?: "Unknown",
                cores = Runtime.getRuntime().availableProcessors(),
                maxFreq = getMaxCpuFreq(),
                architecture = System.getProperty("os.arch") ?: "Unknown",
                currentFreqs = coreFreqs,
                load = calculateCpuLoad(),
                governor = getCpuGovernor()
            )
        } catch (e: Exception) {
            CpuInfo("Unknown", 0, "0 MHz", "Unknown")
        }
    }

    private fun readProcCpuInfo(): Map<String, String> {
        return try {
            File("/proc/cpuinfo").readLines().filter { it.contains(":") }.associate {
                val parts = it.split(":").map { part -> part.trim() }
                parts[0] to parts[1]
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun readCpuCoreFrequency(core: Int): String {
        return try {
            val freqFile = File("/sys/devices/system/cpu/cpu$core/cpufreq/scaling_cur_freq")
            if (freqFile.exists()) {
                "${freqFile.readText().trim().toInt() / 1000} MHz"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getMaxCpuFreq(): String {
        return try {
            val maxFreqFile = File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
            if (maxFreqFile.exists()) {
                "${maxFreqFile.readText().trim().toInt() / 1000} MHz"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun calculateCpuLoad(): CpuInfo.CpuLoad? {
        return try {
            val statLines = File("/proc/stat").readLines()
            val cpuLine = statLines.firstOrNull { it.startsWith("cpu ") } ?: return null
            val parts = cpuLine.split("\\s+".toRegex()).drop(1)
            if (parts.size < 4) return null

            val user = parts[0].toFloat()
            val nice = parts[1].toFloat()
            val system = parts[2].toFloat()
            val idle = parts[3].toFloat()
            val total = user + nice + system + idle

            CpuInfo.CpuLoad(
                user = user,
                system = system,
                idle = idle,
                totalUsage = if (total > 0) ((user + system) / total * 100) else 0f
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getCpuGovernor(): String? {
        return try {
            File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor").readText().trim()
        } catch (e: Exception) {
            null
        }
    }

    // Battery Implementation
    override fun getBatteryInfo(): BatteryInfo {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return BatteryInfo(
            capacity = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString() + "%",
            temperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)?.div(10f) ?: 0f,
            voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)?.div(1000f) ?: 0f,
            health = getBatteryHealth(intent),
            currentNow = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW),
            currentAvg = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE),
            chargeCounter = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER),
            isCharging = isCharging(intent),
            chargeSource = getChargeSource(intent),
            chargeStatus = getChargeStatus(intent),
            technology = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
            healthMetrics = getBatteryHealthMetrics(),
            chargeSpeed = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        )
    }

    private fun getBatteryHealthMetrics(): BatteryInfo.BatteryHealth? {
        return try {
            BatteryInfo.BatteryHealth(
                cycleCount = readSysFileAsInt("/sys/class/power_supply/battery/cycle_count"),
                designCapacity = readSysFileAsInt("/sys/class/power_supply/battery/charge_full_design"),
                currentCapacity = readSysFileAsInt("/sys/class/power_supply/battery/charge_full"),
                wearLevel = calculateBatteryWear()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateBatteryWear(): Float {
        return try {
            val design = readSysFileAsInt("/sys/class/power_supply/battery/charge_full_design")
            val current = readSysFileAsInt("/sys/class/power_supply/battery/charge_full")
            if (design > 0) ((design - current).toFloat() / design) * 100 else 0f
        } catch (e: Exception) {
            0f
        }
    }

    // Memory Implementation
    override fun getMemoryInfo(): MemoryInfo {
        val memInfo = readProcMemInfo()
        return MemoryInfo(
            totalRam = formatMemory(memInfo["MemTotal"]),
            availableRam = formatMemory(memInfo["MemAvailable"]),
            totalSwap = formatMemory(memInfo["SwapTotal"]),
            zramUsage = getZramUsage(),
            details = getMemoryDetails()
        )
    }

    private fun readProcMemInfo(): Map<String, String> {
        return try {
            File("/proc/meminfo").readLines().map { line ->
                val parts = line.split(":").map { it.trim() }
                if (parts.size == 2) parts[0] to parts[1].replace("kB", "").trim() else "" to ""
            }.filter { it.first.isNotEmpty() }.toMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun formatMemory(kbString: String?): String {
        if (kbString == null) return "Unknown"
        return try {
            val kb = kbString.toLong()
            when {
                kb >= 1_048_576 -> "%.1f GB".format(kb / 1_048_576f)
                kb >= 1024 -> "%.1f MB".format(kb / 1024f)
                else -> "$kb KB"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getZramUsage(): String? {
        return try {
            val disksize = File("/sys/block/zram0/disksize").readText().trim().toLong()
            val memUsed = File("/sys/block/zram0/mem_used_total").readText().trim().toLong()
            "%.1f%%".format(memUsed.toFloat() / disksize * 100)
        } catch (e: Exception) {
            null
        }
    }

    private fun getMemoryDetails(): MemoryInfo.MemoryDetails? {
        return try {
            val memInfo = readProcMemInfo()
            MemoryInfo.MemoryDetails(
                active = formatMemory(memInfo["Active"]),
                inactive = formatMemory(memInfo["Inactive"]),
                cached = formatMemory(memInfo["Cached"]),
                buffers = formatMemory(memInfo["Buffers"]),
                swapCached = formatMemory(memInfo["SwapCached"])
            )
        } catch (e: Exception) {
            null
        }
    }

    // Storage Implementation
    override fun getStorageInfo(): StorageInfo {
        val statFs = StatFs(context.filesDir.absolutePath)
        return StorageInfo(
            totalInternal = formatSize(statFs.blockCountLong * statFs.blockSizeLong),
            availableInternal = formatSize(statFs.availableBlocksLong * statFs.blockSizeLong),
            performance = getStoragePerformance(),
            health = getStorageHealth()
        )
    }

    private fun formatSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        return "%.1f %s".format(
            bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups]
        )
    }

    private fun getStoragePerformance(): StorageInfo.StoragePerformance {
        // Placeholder values; real benchmarking would be needed.
        return StorageInfo.StoragePerformance(
            readSpeed = "500 MB/s", writeSpeed = "300 MB/s"
        )
    }

    private fun getStorageHealth(): StorageInfo.StorageHealth? {
        return try {
            // Example for some devices; may not be available on all devices.
            val lifetime = File("/sys/block/sda/device/lifetime").readText().trim().toInt()
            StorageInfo.StorageHealth(
                totalBytesWritten = 0, // Requires privileged access.
                lifespanPercentage = (100 - lifetime).toFloat()
            )
        } catch (e: Exception) {
            null
        }
    }

    // Device Implementation
    override fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            product = Build.PRODUCT,
            hardware = Build.HARDWARE,
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT.toString(),
            security = getDeviceSecurity(),
            rootStatus = checkRootStatus()
        )
    }

    private fun getDeviceSecurity(): DeviceInfo.DeviceSecurity? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DeviceInfo.DeviceSecurity(
                    selinuxStatus = getSelinuxStatus(),
                    verifiedBoot = getVerifiedBootState(),
                    securityPatch = Build.VERSION.SECURITY_PATCH
                )
            } else {
                DeviceInfo.DeviceSecurity(
                    selinuxStatus = getSelinuxStatus(),
                    verifiedBoot = getVerifiedBootState(),
                    securityPatch = "Unknown"
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getSelinuxStatus(): String {
        return try {
            val process = ProcessBuilder("getenforce").start()
            val output = process.inputStream.bufferedReader().readText().trim()
            output.takeIf { it.isNotEmpty() } ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getVerifiedBootState(): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Build.VERSION.SECURITY_PATCH ?: "Unknown"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun checkRootStatus(): DeviceInfo.RootStatus {
        return if (checkRootMethod1() || checkRootMethod2()) {
            DeviceInfo.RootStatus.ROOTED
        } else {
            DeviceInfo.RootStatus.NOT_ROOTED
        }
    }

    private fun checkRootMethod1(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkRootMethod2(): Boolean {
        return try {
            Runtime.getRuntime().exec("su").waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    // Thermal Implementation
    override fun getThermalInfo(): ThermalInfo? {
        return try {
            val zones = File("/sys/class/thermal").listFiles()
                ?.filter { it.name.startsWith("thermal_zone") }?.mapNotNull { zone ->
                    try {
                        val type = File("${zone.path}/type").readText().trim()
                        val tempRaw =
                            File("${zone.path}/temp").readText().trim().toFloatOrNull() ?: 0f
                        // Adjust if device reports in millidegrees.
                        ThermalInfo.ThermalZone(
                            name = zone.name,
                            type = type,
                            temp = if (tempRaw > 1000) tempRaw / 1000 else tempRaw
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
            ThermalInfo(
                zones = zones, throttlingStatus = getThrottlingStatus()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getThrottlingStatus(): String {
        return try {
            val temp = File("/sys/class/thermal/thermal_zone0/temp").readText().trim().toInt()
            when {
                temp > 80000 -> "CRITICAL"
                temp > 70000 -> "HIGH"
                temp > 60000 -> "MEDIUM"
                else -> "NORMAL"
            }
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    // Network Implementation
    override fun getNetworkInfo(): NetworkInfo? {
        val activeNetwork = connectivityManager.activeNetworkInfo
        return try {
            NetworkInfo(
                connectionType = when (activeNetwork?.type) {
                    ConnectivityManager.TYPE_WIFI -> "WiFi"
                    ConnectivityManager.TYPE_MOBILE -> "Mobile"
                    else -> "Unknown"
                },
                signalStrength = getSignalStrength(),
                ipAddress = getIPAddress(),
                dataUsage = NetworkInfo.DataUsage(
                    txBytes = TrafficStats.getTotalTxBytes(),
                    rxBytes = TrafficStats.getTotalRxBytes()
                )
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getSignalStrength(): Int {
        return try {
            (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.rssi
        } catch (e: Exception) {
            -1
        }
    }

    private fun getIPAddress(): String {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) continue
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress ?: "Unknown"
                    }
                }
            }
            "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    // Sensor Implementation
    override fun getSensorInfo(): SensorInfo? {
        return try {
            val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL).map { sensor ->
                SensorInfo.Sensor(
                    name = sensor.name, vendor = sensor.vendor, type = sensor.stringType
                )
            }
            SensorInfo(
                availableSensors = sensors,
                significantMotion = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) != null
            )
        } catch (e: Exception) {
            null
        }
    }

    // Display Implementation
    override fun getDisplayInfo(): DisplayInfo? {
        return try {
            val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY) ?: return null
            val refreshRate = display.refreshRate.toInt()
            val brightness = try {
                Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            } catch (e: Exception) {
                0
            }
            val hdrCapabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                display.hdrCapabilities?.supportedHdrTypes?.map {
                    when (it) {
                        Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> "Dolby Vision"
                        Display.HdrCapabilities.HDR_TYPE_HDR10 -> "HDR10"
                        Display.HdrCapabilities.HDR_TYPE_HLG -> "HLG"
                        else -> "Unknown"
                    }
                } ?: emptyList()
            } else {
                emptyList()
            }
            // Use reflection for lastShutdownTime (available on API 28+)
            val screenOnTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    val method = powerManager.javaClass.getMethod("getLastShutdownTime")
                    val lastShutdownTime = method.invoke(powerManager) as? Long ?: 0L
                    SystemClock.elapsedRealtime() - lastShutdownTime
                } catch (e: Exception) {
                    0L
                }
            } else {
                0L
            }
            DisplayInfo(
                refreshRate = refreshRate,
                brightness = brightness,
                hdrCapabilities = hdrCapabilities,
                screenOnTime = screenOnTime
            )
        } catch (e: Exception) {
            null
        }
    }

    // Security Implementation
    override fun getSecurityInfo(): SecurityInfo? {
        return try {
            SecurityInfo(
                bootloaderStatus = Build.BOOTLOADER,
                googlePlayProtect = isGooglePlayProtectEnabled(),
                encryptionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getEncryptionStatus() else "Unknown"
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun isGooglePlayProtectEnabled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(
                "com.google.android.gms", PackageManager.GET_ACTIVITIES
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    // getEncryptionStatus uses reflection-safe access if necessary.
    private fun getEncryptionStatus(): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (Build.VERSION.SECURITY_PATCH) {
                    "FBE" -> "File-Based"
                    "FDE" -> "Full-Disk"
                    else -> "Unknown"
                }
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    // Helper method to read system files as Int.
    private fun readSysFileAsInt(path: String): Int {
        return try {
            File(path).readText().trim().toInt()
        } catch (e: Exception) {
            -1
        }
    }

    // Battery helper methods
    private fun getBatteryHealth(intent: Intent?): String {
        return when (intent?.getIntExtra(
            BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN
        )) {
            BatteryManager.BATTERY_HEALTH_COLD -> "COLD"
            BatteryManager.BATTERY_HEALTH_DEAD -> "DEAD"
            BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER_VOLTAGE"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEAT"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "FAILURE"
            else -> "UNKNOWN"
        }
    }

    private fun isCharging(intent: Intent?): Boolean {
        return when (intent?.getIntExtra(
            BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN
        )) {
            BatteryManager.BATTERY_STATUS_CHARGING, BatteryManager.BATTERY_STATUS_FULL -> true
            else -> false
        }
    }

    private fun getChargeSource(intent: Intent?): String {
        return when (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"
            else -> "UNPLUGGED"
        }
    }

    private fun getChargeStatus(intent: Intent?): String {
        return when (intent?.getIntExtra(
            BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN
        )) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "DISCHARGING"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NOT_CHARGING"
            BatteryManager.BATTERY_STATUS_FULL -> "FULL"
            else -> "UNKNOWN"
        }
    }
}
