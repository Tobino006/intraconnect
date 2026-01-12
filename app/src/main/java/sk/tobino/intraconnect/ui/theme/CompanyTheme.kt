package sk.tobino.intraconnect.ui.theme

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

@Composable
fun CompanyTheme(
    companyColorHex: String?,
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    val primaryColor = companyColorHex?.let { hex ->
        try {
            Color(hex.toColorInt())
        } catch (e: IllegalArgumentException) {
            Log.e("CompanyTheme", "Invalid color hex: $hex", e)
            LightPrimary
        }
    } ?: LightPrimary

    val colorScheme =
        if (darkTheme) {
            darkColorScheme(
                primary = primaryColor,
                secondary = primaryColor,
                onPrimary = DarkOnPrimary,
                background = DarkBackground,
                onBackground = DarkOnBackground,
            )
        } else {
            lightColorScheme(
                primary = primaryColor,
                secondary = primaryColor,
                onPrimary = LightOnPrimary,
                background = LightBackground,
                onBackground = LightOnBackground,
            )
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
