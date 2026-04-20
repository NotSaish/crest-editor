package com.cosmic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CosmicColorScheme = darkColorScheme(
    primary = CosmicPurple,
    onPrimary = CosmicWhite,
    secondary = CosmicBlue,
    onSecondary = CosmicWhite,
    tertiary = CosmicCyan,
    background = CosmicBlack,
    onBackground = CosmicWhite,
    surface = CosmicSurface,
    onSurface = CosmicWhite,
    surfaceVariant = CosmicCard,
    onSurfaceVariant = CosmicGrayLight,
    error = CosmicRed,
    onError = CosmicWhite,
)

@Composable
fun CosmicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CosmicColorScheme,
        typography = CosmicTypography,
        content = content
    )
}
