package me.riafy.finlog.utils

import androidx.compose.ui.graphics.Color

/** Parses a "#RRGGBB" string as stored on a category. Falls back to a neutral grey if malformed. */
fun parseHexColor(hex: String): Color {
    val rgb = hex.removePrefix("#").toLongOrNull(16) ?: return Color(0xFF9AA3B8)
    return Color(0xFF000000 or rgb)
}
