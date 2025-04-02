package io.android.pixelspec.presentation.component

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import io.android.pixelspec.presentation.model.HardwareState

@Composable
fun HardwareInfoContent(
    state: HardwareState.Success, padding: PaddingValues, context: Context
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        item { ProcessorSection(state.cpuInfo) }
        item { BatterySection(state.batteryInfo) }
        item { MemorySection(state.memoryInfo) }
        item { StorageSection(state.storageInfo) }
        item { DeviceSection(state.deviceInfo) }

        state.thermalInfo?.let { thermal ->
            item { ThermalSection(thermal) }
        }

        state.networkInfo?.let { network ->
            item { NetworkSection(network) }
        }

        state.sensorInfo?.let { sensors ->
            item { SensorSection(sensors) }
        }

        state.displayInfo?.let { display ->
            item { DisplaySection(display) }
        }

        state.securityInfo?.let { security ->
            item { SecuritySection(security) }
        }
    }
}

@Composable
private fun ProcessorSection(cpuInfo: CpuInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Processor")
            InfoRow("Model", cpuInfo.name)
            InfoRow("Cores", cpuInfo.cores.toString())
            InfoRow("Max Frequency", cpuInfo.maxFreq)
            InfoRow("Architecture", cpuInfo.architecture)

            cpuInfo.load?.let { load ->
                InfoRow("CPU Load", "%.1f%%".format(load.totalUsage))
            }

            cpuInfo.temperature?.let { temp ->
                InfoRow("Temperature", "%.1f°C".format(temp))
            }

            cpuInfo.governor?.let { governor ->
                InfoRow("Governor", governor)
            }

            if (cpuInfo.currentFreqs.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Core Frequencies")
                cpuInfo.currentFreqs.forEachIndexed { index, freq ->
                    InfoRow("Core $index", freq)
                }
            }
        }
    }
}

@Composable
private fun BatterySection(batteryInfo: BatteryInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Battery")
            InfoRow("Capacity", batteryInfo.capacity)
            InfoRow("Temperature", "%.1f°C".format(batteryInfo.temperature))
            InfoRow("Voltage", "%.2f V".format(batteryInfo.voltage))
            InfoRow("Health", batteryInfo.health)
            InfoRow("Status", batteryInfo.chargeStatus)
            InfoRow("Source", batteryInfo.chargeSource)
            InfoRow("Technology", batteryInfo.technology ?: "Unknown")

            batteryInfo.healthMetrics?.let { health ->
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Health Details")
                InfoRow("Cycle Count", health.cycleCount.toString())
                InfoRow("Design Capacity", "${health.designCapacity}mAh")
                InfoRow("Current Capacity", "${health.currentCapacity}mAh")
                InfoRow("Wear Level", "%.1f%%".format(health.wearLevel))
            }
        }
    }
}

@Composable
private fun MemorySection(memoryInfo: MemoryInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Memory")
            InfoRow("Total RAM", memoryInfo.totalRam)
            InfoRow("Available RAM", memoryInfo.availableRam)
            InfoRow("Total Swap", memoryInfo.totalSwap)

            memoryInfo.zramUsage?.let { zram ->
                InfoRow("ZRAM Usage", zram)
            }

            memoryInfo.details?.let { details ->
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Detailed Usage")
                InfoRow("Active", details.active)
                InfoRow("Inactive", details.inactive)
                InfoRow("Cached", details.cached)
                InfoRow("Buffers", details.buffers)
                InfoRow("Swap Cached", details.swapCached)
            }
        }
    }
}

@Composable
private fun StorageSection(storageInfo: StorageInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Storage")
            InfoRow("Total Internal", storageInfo.totalInternal)
            InfoRow("Available Internal", storageInfo.availableInternal)

            storageInfo.performance?.let { perf ->
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Performance")
                InfoRow("Read Speed", perf.readSpeed)
                InfoRow("Write Speed", perf.writeSpeed)
            }

            storageInfo.health?.let { health ->
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Health")
                InfoRow("Lifespan", "%.1f%%".format(health.lifespanPercentage))
            }
        }
    }
}

@Composable
private fun DeviceSection(deviceInfo: DeviceInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Device")
            InfoRow("Manufacturer", deviceInfo.manufacturer)
            InfoRow("Model", deviceInfo.model)
            InfoRow("Android Version", deviceInfo.androidVersion)
            InfoRow("SDK Version", deviceInfo.sdkVersion)

            deviceInfo.security?.let { security ->
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Security")
                InfoRow("SELinux", security.selinuxStatus)
                InfoRow("Verified Boot", security.verifiedBoot)
                InfoRow("Security Patch", security.securityPatch)
            }

            deviceInfo.rootStatus?.let { root ->
                InfoRow("Root Status", root.name)
            }
        }
    }
}

@Composable
private fun ThermalSection(thermalInfo: ThermalInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Thermal")
            InfoRow("Throttling Status", thermalInfo.throttlingStatus)

            if (thermalInfo.zones.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Thermal Zones")
                thermalInfo.zones.forEach { zone ->
                    InfoRow(
                        label = "${zone.name} (${zone.type})", value = "%.1f°C".format(zone.temp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworkSection(networkInfo: NetworkInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Network")
            InfoRow("Connection Type", networkInfo.connectionType)
            InfoRow("Signal Strength", "${networkInfo.signalStrength} dBm")
            InfoRow("IP Address", networkInfo.ipAddress)

            networkInfo.dataUsage.let { usage ->
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Data Usage")
                InfoRow("Sent", formatBytes(usage.txBytes))
                InfoRow("Received", formatBytes(usage.rxBytes))
            }
        }
    }
}

@Composable
private fun SensorSection(sensorInfo: SensorInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Sensors")
            InfoRow("Total Sensors", sensorInfo.availableSensors.size.toString())
            InfoRow("Significant Motion", sensorInfo.significantMotion.toString())

            if (sensorInfo.availableSensors.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("Available Sensors")
                sensorInfo.availableSensors.take(5).forEach { sensor ->
                    InfoRow(sensor.type, sensor.name)
                }
            }
        }
    }
}

@Composable
private fun DisplaySection(displayInfo: DisplayInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Display")
            InfoRow("Refresh Rate", "${displayInfo.refreshRate}Hz")
            InfoRow("Brightness", "${displayInfo.brightness}/255")
            InfoRow("Screen On Time", formatMillis(displayInfo.screenOnTime))

            if (displayInfo.hdrCapabilities.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(4.dp))
                SubSectionTitle("HDR Support")
                displayInfo.hdrCapabilities.forEach { hdr ->
                    InfoRow(hdr, "Supported")
                }
            }
        }
    }
}

@Composable
private fun SecuritySection(securityInfo: SecurityInfo) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Security")
            InfoRow("Bootloader", securityInfo.bootloaderStatus)
            InfoRow("Google Play Protect", securityInfo.googlePlayProtect.toString())
            InfoRow("Encryption", securityInfo.encryptionStatus)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SubSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

private fun formatBytes(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    if (bytes <= 0) return "0 B"
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return "%.1f %s".format(bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

private fun formatMillis(millis: Long): String {
    val seconds = millis / 1000 % 60
    val minutes = millis / (1000 * 60) % 60
    val hours = millis / (1000 * 60 * 60)
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}