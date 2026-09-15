package me.riafy.finlog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import me.riafy.finlog.data.local.preference.AppPreference

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = SurfaceLight,
    primaryContainer = TealContainerLight,
    onPrimaryContainer = TealOnContainerLight,
    secondary = Slate,
    onSecondary = SurfaceLight,
    secondaryContainer = SlateContainerLight,
    onSecondaryContainer = SlateOnContainerLight,
    tertiary = Amber,
    onTertiary = Ink,
    background = PageLight,
    onBackground = Ink,
    surface = SurfaceLight,
    onSurface = Ink,
    surfaceVariant = PageLight,
    onSurfaceVariant = InkMuted,
    outline = OutlineLight,
    outlineVariant = OutlineLight,
    error = ErrorRed,
    onError = SurfaceLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight
)

private val DarkColors = darkColorScheme(
    primary = TealSoft,
    onPrimary = PageDark,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = OnDark,
    secondary = Slate,
    onSecondary = PageDark,
    secondaryContainer = SlateContainerDark,
    onSecondaryContainer = SlateOnContainerDark,
    tertiary = Amber,
    onTertiary = PageDark,
    background = PageDark,
    onBackground = OnDark,
    surface = SurfaceDark,
    onSurface = OnDark,
    surfaceVariant = SurfaceDarkRaised,
    onSurfaceVariant = OnDarkMuted,
    outline = OutlineDark,
    outlineVariant = OutlineDark,
    error = ErrorRed,
    onError = PageDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark
)

/** Resolves the user's theme choice against the system setting. */
@Composable
fun shouldUseDarkTheme(themeMode: String): Boolean = when (themeMode) {
    AppPreference.THEME_LIGHT -> false
    AppPreference.THEME_DARK -> true
    else -> isSystemInDarkTheme()
}

@Composable
fun FinlogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
