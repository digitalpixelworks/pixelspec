package io.android.pixelspec.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BatteryInfoCard(
    capacity: String,
    voltage: String,
    temperature: String,
    health: String,
    technology: String,
    current: String,
    averageCurrent: String,
    isCharging: Boolean,
    chargeStatus: String,
    chargeSource: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Use derivedStateOf for frequently changing values
            val chargingStatus by remember(isCharging) {
                derivedStateOf {
                    if (isCharging) "Charging (${chargeSource})"
                    else "Discharging"
                }
            }
            Text(
                "Battery Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            InfoRow("Capacity", capacity)
            InfoRow("Voltage", voltage)
            InfoRow("Temperature", temperature)
            InfoRow("Health", health)
            InfoRow("Technology", technology)
            InfoRow("Current", current)
            InfoRow("Average Current", averageCurrent)
            InfoRow("Status", chargingStatus)
            InfoRow("Charge Status", chargeStatus)
        }
    }
}