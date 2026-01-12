package sk.tobino.intraconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme (
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    background = LightBackground,
    onBackground = LightOnBackground,
)

private val DarkColors = darkColorScheme (
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()

    MaterialTheme (
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}