package io.android.pixelspec.presentation.screen.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.android.pixelspec.domain.usecase.GetHardwareInfoUseCase
import io.android.pixelspec.presentation.model.HardwareState
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HardwareViewModel @Inject constructor(
    private val getHardwareInfo: GetHardwareInfoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = mutableStateOf<HardwareState>(HardwareState.Loading)
    val state: State<HardwareState> = _state

    private var currentSuccessState: HardwareState.Success? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { handleBatteryUpdate(it) }
        }
    }

    init {
        loadHardwareInfo()
        registerBatteryReceiver()
    }

    private fun loadHardwareInfo() {
        viewModelScope.launch {
            _state.value = HardwareState.Loading
            try {
                val info = getHardwareInfo()
                val batteryInfo = info.battery.copy(
                    capacity = getCurrentBatteryPercentage().toString()
                )
                val newState = HardwareState.Success(
                    cpuInfo = info.cpu,
                    batteryInfo = batteryInfo,
                    memoryInfo = info.memory,
                    storageInfo = info.storage,
                    deviceInfo = info.device,
                    thermalInfo = info.thermal,
                    networkInfo = info.network,
                    sensorInfo = info.sensors,
                    displayInfo = info.display,
                    securityInfo = info.security
                )
                currentSuccessState = newState
                _state.value = newState
            } catch (e: Exception) {
                _state.value = HardwareState.Error(
                    message = e.message ?: "Unknown error occurred",
                    previousData = currentSuccessState
                )
            }
        }
    }

    private fun registerBatteryReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        context.registerReceiver(batteryReceiver, filter)
    }

    private fun handleBatteryUpdate(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                updateBatteryFromIntent(intent)
            }

            Intent.ACTION_POWER_CONNECTED -> {
                updateBatteryStatus(
                    isCharging = true,
                    chargeSource = getChargeSource(intent),
                    chargeStatus = "CHARGING"
                )
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                updateBatteryStatus(
                    isCharging = false, chargeStatus = "DISCHARGING"
                )
            }
        }
    }

    private fun updateBatteryFromIntent(intent: Intent) {
        val batteryPct = getCurrentBatteryPercentage()
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000f
        val health = when (intent.getIntExtra(
            BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN
        )) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
            else -> "UNKNOWN"
        }

        updateBatteryStatus(
            capacity = "$batteryPct%",
            temperature = temperature,
            voltage = voltage,
            health = health,
            isCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0,
            chargeSource = getChargeSource(intent)
        )
    }

    private fun getCurrentBatteryPercentage(): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        return batteryStatus?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (level * 100 / scale.toFloat()).toInt()
        } ?: -1
    }

    private fun getChargeSource(intent: Intent): String {
        return when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"
            else -> "UNKNOWN"
        }
    }

    private fun updateBatteryStatus(
        capacity: String? = null,
        temperature: Float? = null,
        voltage: Float? = null,
        health: String? = null,
        isCharging: Boolean? = null,
        chargeSource: String? = null,
        chargeStatus: String? = null
    ) {
        when (val currentState = _state.value) {
            is HardwareState.Success -> {
                val currentBattery = currentState.batteryInfo
                val updatedBattery = currentBattery.copy(
                    capacity = capacity ?: currentBattery.capacity,
                    temperature = temperature ?: currentBattery.temperature,
                    voltage = voltage ?: currentBattery.voltage,
                    health = health ?: currentBattery.health,
                    isCharging = isCharging ?: currentBattery.isCharging,
                    chargeSource = chargeSource ?: currentBattery.chargeSource,
                    chargeStatus = chargeStatus ?: currentBattery.chargeStatus
                )
                val newState = currentState.copy(batteryInfo = updatedBattery)
                currentSuccessState = newState
                _state.value = newState
            }

            is HardwareState.Error -> {
                currentState.previousData?.let { previousData ->
                    val currentBattery = previousData.batteryInfo
                    val updatedBattery = currentBattery.copy(
                        capacity = capacity ?: currentBattery.capacity,
                        temperature = temperature ?: currentBattery.temperature,
                        voltage = voltage ?: currentBattery.voltage,
                        health = health ?: currentBattery.health,
                        isCharging = isCharging ?: currentBattery.isCharging,
                        chargeSource = chargeSource ?: currentBattery.chargeSource,
                        chargeStatus = chargeStatus ?: currentBattery.chargeStatus
                    )
                    currentSuccessState = previousData.copy(batteryInfo = updatedBattery)
                }
            }

            HardwareState.Loading -> { /* Still loading */
            }
        }
    }

    override fun onCleared() {
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered
        }
        super.onCleared()
    }
}