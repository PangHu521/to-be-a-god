package com.deify.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DeifyColorScheme = darkColorScheme(
    primary = Green500,
    onPrimary = DarkBg,
    primaryContainer = Green400,
    secondary = Orange500,
    onSecondary = DarkBg,
    error = Red400,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted
)

@Composable
fun DeifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DeifyColorScheme,
        typography = DeifyTypography,
        content = content
    )
}
