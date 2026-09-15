package me.riafy.finlog.ui.theme

import androidx.compose.ui.graphics.Color

// Neutrals - light
val Ink = Color(0xFF10182B)
val InkMuted = Color(0xFF5B6478)
val InkSubtle = Color(0xFF97A0B4)
val PageLight = Color(0xFFF7F8FA)
val SurfaceLight = Color(0xFFFFFFFF)
val OutlineLight = Color(0xFFE6E9EF)

// Elevated-surface tones - light. Material3 derives these from a purple-leaning
// baseline when a color scheme doesn't specify them, which shows up on things
// like the bottom navigation bar; naming them keeps every surface neutral.
val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF3F4F7)
val SurfaceContainerLight = Color(0xFFEFF0F4)
val SurfaceContainerHighLight = Color(0xFFE9EAEF)
val SurfaceContainerHighestLight = Color(0xFFE3E5EB)

// Neutrals - dark
val PageDark = Color(0xFF0B0F1A)
val SurfaceDark = Color(0xFF141924)
val SurfaceDarkRaised = Color(0xFF1C2230)
val OnDark = Color(0xFFF1F3F7)
val OnDarkMuted = Color(0xFF9AA3B8)
val OutlineDark = Color(0xFF262D3D)

// Elevated-surface tones - dark, same reasoning as the light set above.
val SurfaceContainerLowestDark = Color(0xFF060910)
val SurfaceContainerLowDark = Color(0xFF10141F)
val SurfaceContainerDark = Color(0xFF1C2230)
val SurfaceContainerHighDark = Color(0xFF222A3D)
val SurfaceContainerHighestDark = Color(0xFF29334A)

// Brand - a restrained teal rather than the obvious "money green"
val Teal = Color(0xFF0E7C66)
val TealSoft = Color(0xFF4FB69E)
val Slate = Color(0xFF5B6478)
val Amber = Color(0xFFE8A33D)

// Semantic
val Success = Color(0xFF1F9D66)
val Warning = Color(0xFFE8A33D)
val ErrorRed = Color(0xFFE5484D)
val IncomeGreen = Color(0xFF1F9D66)
val ExpenseCoral = Color(0xFFE5484D)

// Container tints used by the Material colour schemes
val TealContainerLight = Color(0xFFDCF2EC)
val TealOnContainerLight = Color(0xFF07473B)
val TealContainerDark = Color(0xFF16362E)

// secondaryContainer tints. Left unset, Material3's baseline scheme derives a
// lilac tone here that has nothing to do with this palette - components like
// SegmentedButton and the nav bar indicator pick it up by default, so it needs
// an explicit, on-brand value the same as every other role.
val SlateContainerLight = Color(0xFFE3E6ED)
val SlateOnContainerLight = Color(0xFF2B3242)
val SlateContainerDark = Color(0xFF313A4E)
val SlateOnContainerDark = Color(0xFFDCE1EC)

// Category accent palette assigned to default categories, and offered when a
// user creates their own. Kept distinct enough to scan at a glance in a list.
val CategoryPalette = listOf(
    Color(0xFFE8734D), // Food
    Color(0xFF4C8DFF), // Transport
    Color(0xFFB05FD1), // Shopping
    Color(0xFFE8A33D), // Bills
    Color(0xFFD64B8F), // Entertainment
    Color(0xFF2FA8A0), // Health
    Color(0xFF4FB669), // Travel
    Color(0xFF6B7FE0), // Subscriptions
    Color(0xFF3DA5D9), // Education
    Color(0xFF8A93A6), // Personal
    Color(0xFF9AA3B8)  // Other
)
