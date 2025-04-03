package io.android.pixelspec.presentation.screen.home

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdView
import io.android.pixelspec.presentation.component.CenterLoading
import io.android.pixelspec.presentation.component.ErrorMessage
import io.android.pixelspec.presentation.component.HardwareInfoContent
import io.android.pixelspec.presentation.model.HardwareState
import io.android.pixelspec.presentation.util.AdManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAppsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HardwareViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val context = LocalContext.current

    val adView = remember { AdView(context) }
    val adManager = remember { AdManager(context) }
    var showAd by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        // Delay ad loading to prevent ANR during initial render
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            showAd = true
            adManager.loadBannerAd(adView)
        }, 1000) // 1 second delay

        onDispose {
            adManager.destroy()
            adView.destroy()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("PixelSpec") }, actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "Settings")
            }
            IconButton(onClick = onAppsClick) {
                Icon(Icons.Default.Apps, "Installed Apps")
            }
        })
    }, bottomBar = {
        if (showAd && LocalInspectionMode.current.not()) {
            AndroidView(
                factory = { adView }, modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            )
        }
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