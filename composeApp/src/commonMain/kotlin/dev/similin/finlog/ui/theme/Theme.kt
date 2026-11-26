package dev.similin.finlog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import dev.similin.finlog.data.local.preference.AppPreference

// Signal yellow carries the things that matter right now: primary actions,
// the active tab, and the summary band. Neutrals do everything else.
private val LightColors = lightColorScheme(
    primary = SignYellow,
    onPrimary = Ink,
    primaryContainer = SignYellow,
    onPrimaryContainer = Ink,
    secondary = InkMuted,
    onSecondary = SurfaceLight,
    secondaryContainer = SignYellow,
    onSecondaryContainer = Ink,
    tertiary = Warning,
    onTertiary = SurfaceLight,
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
    primary = SignYellow,
    onPrimary = SignBlack,
    primaryContainer = SignYellow,
    onPrimaryContainer = SignBlack,
    secondary = OnDarkMuted,
    onSecondary = PageDark,
    secondaryContainer = SignYellow,
    onSecondaryContainer = SignBlack,
    tertiary = Warning,
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
