package me.riafy.finlog.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Android cannot blur arbitrary app content behind a view. Modifier.blur blurs a
 * composable's own content, and Window.setBackgroundBlurRadius applies only to
 * what sits behind a separate window. The bar therefore falls back to a tinted
 * translucent surface - documented behaviour, not a missing feature.
 */
@Composable
actual fun supportsBackdropBlur(): Boolean = false

@Composable
actual fun BackdropBlur(modifier: Modifier, isDark: Boolean) = Unit
