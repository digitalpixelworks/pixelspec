package io.android.pixelspec.presentation.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import io.android.pixelspec.presentation.component.CenterLoading
import io.android.pixelspec.presentation.component.ErrorMessage
import io.android.pixelspec.presentation.component.HardwareInfoContent
import io.android.pixelspec.presentation.model.HardwareState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAppsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HardwareViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PixelSpec") }, actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, "Settings")
                }
                IconButton(onClick = onAppsClick) {
                    Icon(Icons.Default.Apps, "Installed Apps")
                }
            })
        }) { padding ->
        when (state) {
            is HardwareState.Loading -> CenterLoading()
            is HardwareState.Error -> {
                val errorState = state as HardwareState.Error
                ErrorMessage(errorState.message)
                errorState.previousData?.let { previousData ->
                    HardwareInfoContent(previousData, padding, context)
                }
            }

            is HardwareState.Success -> {
                val successState = state as HardwareState.Success
                HardwareInfoContent(successState, padding, context)
            }
        }
    }
}
