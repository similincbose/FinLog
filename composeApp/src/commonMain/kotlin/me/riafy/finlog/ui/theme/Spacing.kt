package me.riafy.finlog.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Centralized spacing scale so screens don't invent their own dp values. */
object Spacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp

    /** Bottom padding a scrollable screen needs so its last row clears the tab bar. */
    val bottomBarClearance: Dp = 96.dp
}
