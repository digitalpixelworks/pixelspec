package io.android.pixelspec.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.android.pixelspec.R
import io.android.pixelspec.domain.model.ThemeMode

@Composable
fun ThemeSelectionItem(
    currentMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ThemeOption(
            text = stringResource(R.string.light_theme),
            selected = currentMode == ThemeMode.LIGHT,
            onSelect = { onThemeSelected(ThemeMode.LIGHT) }
        )
        ThemeOption(
            text = stringResource(R.string.dark_theme),
            selected = currentMode == ThemeMode.DARK,
            onSelect = { onThemeSelected(ThemeMode.DARK) }
        )
        ThemeOption(
            text = stringResource(R.string.system_theme),
            selected = currentMode == ThemeMode.SYSTEM,
            onSelect = { onThemeSelected(ThemeMode.SYSTEM) }
        )
    }
}

@Composable
private fun ThemeOption(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // Handled by the row click
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}