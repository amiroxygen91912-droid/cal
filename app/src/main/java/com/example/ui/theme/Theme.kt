package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PersianGold,
    secondary = PersianTurquoise,
    tertiary = PersianBlue,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color(0xFF050B18),
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = PersianTurquoise,
    tertiary = PersianGold,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightTextSecondary
)

@Composable
fun MyApplicationTheme(
    themeSetting: String = "auto",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeSetting) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
