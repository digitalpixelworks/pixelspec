package io.android.pixelspec.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.android.pixelspec.domain.model.AppInfo

@Composable
fun AppListItem(
    app: AppInfo, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = app.icon, contentDescription = app.name, modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = app.name, style = MaterialTheme.typography.titleMedium, maxLines = 1
            )
            Text(
                text = app.packageName, style = MaterialTheme.typography.bodySmall, maxLines = 1
            )
            Text(
                text = "Version: ${app.version}", style = MaterialTheme.typography.labelSmall
            )
        }
    }
}