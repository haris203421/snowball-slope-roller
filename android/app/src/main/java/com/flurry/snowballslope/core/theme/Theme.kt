package com.flurry.snowballslope.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

const val PRESET_NAME = "OCEAN_FRESH"

private val SnowColorScheme = lightColorScheme(
    primary = GlacierPrimary,
    onPrimary = PureSnow,
    primaryContainer = FrostSurface,
    onPrimaryContainer = NightSlope,
    secondary = FrostSurface,
    onSecondary = NightSlope,
    secondaryContainer = SnowBackgroundDeep,
    onSecondaryContainer = NightSlope,
    tertiary = ScarfRed,
    onTertiary = PureSnow,
    background = SnowBackground,
    onBackground = NightSlope,
    surface = PureSnow,
    onSurface = NightSlope,
    surfaceVariant = FrostSurface,
    onSurfaceVariant = SlateText,
    outline = IceEdge,
    outlineVariant = FrostSurfaceDeep,
    error = ScarfRed,
    onError = PureSnow
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SnowColorScheme,
        typography = AppTypography,
        content = content
    )
}
