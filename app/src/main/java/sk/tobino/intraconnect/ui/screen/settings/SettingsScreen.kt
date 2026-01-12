package sk.tobino.intraconnect.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import sk.tobino.intraconnect.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import sk.tobino.intraconnect.ui.theme.ThemeMode

// main settings screen
@Composable
fun SettingsScreen (
    state: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    if (state.isLoading) {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
        }
        return
    }

    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ) {

        item {
            ThemePreferenceItem (
                themeMode = state.themeMode,
                onThemeChange = onThemeModeChange
            )
        }

        // TODO("other settings")
    }
}

@Composable
private fun ThemePreferenceItem (
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon (
            imageVector = Icons.Filled.Palette,
            contentDescription = null,
        )

        Column (
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text (
                text = stringResource(R.string.settings_use_company_theme),
                style = MaterialTheme.typography.bodyLarge
            )
            Text (
                text = stringResource(R.string.settings_use_company_theme_comment),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        Switch (
            checked = themeMode == ThemeMode.COMPANY,
            onCheckedChange = { checked ->
                onThemeChange (
                    if (checked) ThemeMode.COMPANY else ThemeMode.APP
                )
            }
        )
    }

    HorizontalDivider()
}