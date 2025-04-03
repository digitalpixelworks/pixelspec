package io.android.pixelspec.presentation.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.android.pixelspec.BuildConfig
import io.android.pixelspec.R
import io.android.pixelspec.presentation.component.PreferenceItem
import io.android.pixelspec.presentation.component.SectionTitle
import io.android.pixelspec.presentation.component.ThemeSelectionItem
import io.android.pixelspec.presentation.util.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeState by viewModel.themeState.collectAsState()
    val context = LocalContext.current

    val licenseUrl = stringResource(R.string.license_url)
    val privacyPolicyUrl = stringResource(R.string.privacy_policy_url)
    val termsOfServiceUrl = stringResource(R.string.terms_of_service_url)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings)) }, navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            })
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                SectionTitle(
                    title = stringResource(R.string.appearance), modifier = Modifier.padding(16.dp)
                )
                ThemeSelectionItem(
                    currentMode = themeState.themeMode, onThemeSelected = viewModel::updateThemeMode
                )

                SectionTitle(
                    title = stringResource(R.string.about), modifier = Modifier.padding(16.dp)
                )
                PreferenceItem(
                    title = stringResource(R.string.version), subtitle = BuildConfig.VERSION_NAME
                )
                PreferenceItem(
                    title = stringResource(R.string.license),
                    onClick = { context.openUrl(licenseUrl) })
                PreferenceItem(
                    title = stringResource(R.string.privacy_policy),
                    onClick = { context.openUrl(privacyPolicyUrl) })
                PreferenceItem(
                    title = stringResource(R.string.terms_of_service),
                    onClick = { context.openUrl(termsOfServiceUrl) })
            }

            // Footer
            Text(
                text = "Developed with ❤️ by DigitalPixelWorks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}