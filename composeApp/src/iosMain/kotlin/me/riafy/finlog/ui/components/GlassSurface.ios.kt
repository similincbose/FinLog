package me.riafy.finlog.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSClassFromString
import platform.UIKit.UIBlurEffect
import platform.UIKit.UIBlurEffectStyle
import platform.UIKit.UIGlassEffect
import platform.UIKit.UIGlassEffectStyle
import platform.UIKit.UIVisualEffect
import platform.UIKit.UIVisualEffectView

@Composable
actual fun supportsBackdropBlur(): Boolean = true

/**
 * A real UIVisualEffectView behind the Compose content, so the tab bar blurs
 * whatever is scrolling underneath it.
 *
 * Uses iOS 26's actual Liquid Glass material (UIGlassEffect) where it exists -
 * this is a genuinely different, newer API from UIBlurEffect, not just another
 * blur style, and is what the real system tab bars and Control Center use now.
 * Falls back to the older UIBlurEffect system material on pre-26 iOS, since
 * this app's deployment target (15.0) still has to run there and UIGlassEffect
 * doesn't exist as a class at all on those OS versions.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun BackdropBlur(modifier: Modifier, isDark: Boolean) {
    UIKitView(
        factory = {
            val effect: UIVisualEffect = if (NSClassFromString("UIGlassEffect") != null) {
                UIGlassEffect.effectWithStyle(UIGlassEffectStyle.UIGlassEffectStyleRegular)
            } else {
                val style = if (isDark) {
                    UIBlurEffectStyle.UIBlurEffectStyleSystemMaterialDark
                } else {
                    UIBlurEffectStyle.UIBlurEffectStyleSystemMaterialLight
                }
                UIBlurEffect.effectWithStyle(style)
            }
            UIVisualEffectView(effect = effect).apply {
                setUserInteractionEnabled(false)
            }
        },
        modifier = modifier
    )
}
