package dev.similin.finlog.ui.theme

import androidx.compose.ui.graphics.Color

// Airport wayfinding. The yellow is reserved for "where to go / what matters
// now" - primary actions, the active tab, the summary band - and nothing else.
// Everything else is a quiet neutral so the yellow keeps its meaning.

// Signal yellow, the sign-band ground
val SignYellow = Color(0xFFFFC800)
val SignYellowPressed = Color(0xFFE6B300)
val SignYellowSoft = Color(0xFFFFF1B8)
val SignBlack = Color(0xFF111111)

// Neutrals - light
val Ink = Color(0xFF111111)
val InkMuted = Color(0xFF4A4D55)
val InkSubtle = Color(0xFF7A7E88)
val PageLight = Color(0xFFF2F2F0)
val SurfaceLight = Color(0xFFFFFFFF)
val OutlineLight = Color(0xFFD4D4D0)

// Elevated-surface tones - light
val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF7F7F5)
val SurfaceContainerLight = Color(0xFFEDEDEA)
val SurfaceContainerHighLight = Color(0xFFE6E6E2)
val SurfaceContainerHighestLight = Color(0xFFDEDED9)

// Neutrals - dark
val PageDark = Color(0xFF0B0B0B)
val SurfaceDark = Color(0xFF161616)
val SurfaceDarkRaised = Color(0xFF202020)
val OnDark = Color(0xFFF5F5F2)
val OnDarkMuted = Color(0xFFA6A8AF)
val OutlineDark = Color(0xFF333333)

// Elevated-surface tones - dark
val SurfaceContainerLowestDark = Color(0xFF050505)
val SurfaceContainerLowDark = Color(0xFF121212)
val SurfaceContainerDark = Color(0xFF1B1B1B)
val SurfaceContainerHighDark = Color(0xFF252525)
val SurfaceContainerHighestDark = Color(0xFF2E2E2E)

// Semantic
val Success = Color(0xFF1E8A4C)
val Warning = Color(0xFFB87A00)
val ErrorRed = Color(0xFFD7262E)
val IncomeGreen = Color(0xFF1E8A4C)
val ExpenseCoral = Color(0xFFD7262E)

// Secondary container tones, kept neutral so only yellow reads as "active"
val SlateContainerLight = Color(0xFFE3E3DF)
val SlateOnContainerLight = Color(0xFF222222)
val SlateContainerDark = Color(0xFF2C2C2C)
val SlateOnContainerDark = Color(0xFFE6E6E2)

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
