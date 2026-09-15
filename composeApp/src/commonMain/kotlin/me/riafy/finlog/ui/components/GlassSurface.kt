package me.riafy.finlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Whether the platform can blur what is behind a surface.
 *
 * iOS has UIVisualEffectView. Android has no equivalent for blurring arbitrary
 * app content behind a view - Modifier.blur only blurs a composable's own
 * content, and window blur is limited to separate windows - so Android uses a
 * tinted translucent surface instead.
 */
@Composable
expect fun supportsBackdropBlur(): Boolean

/**
 * The real platform blur, drawn behind [content]. A no-op where unsupported,
 * which is why every caller must also paint a translucent base.
 */
@Composable
expect fun BackdropBlur(modifier: Modifier = Modifier, isDark: Boolean)

/**
 * A frosted surface - the floating tab bar material used by Netflix and Prime
 * Video on iOS, and the closest Android equivalent.
 *
 * Where the platform can blur its backdrop, this is real glass. Where it cannot,
 * the same construction - translucent base, top-lit gradient, hairline border -
 * still reads as a distinct floating layer, just without the blur. The look is
 * deliberately identical in structure so the two platforms stay recognisably the
 * same product.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    cornerRadius: Dp = 28.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    // Real Liquid Glass (iOS 26's UIGlassEffect) already has its own luminosity,
    // reflection and adaptive tinting baked in - painting a flat colour wash on
    // top of it doesn't add "glassiness", it just mutes the material's own
    // quality back towards a plain tinted card. This stays close to bare where
    // there's real blur, and only Android's fallback (no blur at all) needs to
    // carry the surface on its own with a much more solid tint.
    val baseAlpha = when {
        !supportsBackdropBlur() -> 0.88f
        isDark -> 0.10f
        else -> 0.05f
    }
    val base = if (isDark) Color(0xFF0B0F1A) else Color(0xFFFFFFFF)

    // Dark mode needs far less sheen: the platform material already lightens the
    // surface, and more white on top of that reads grey rather than glassy.
    val sheen = if (isDark) {
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.05f), Color.White.copy(alpha = 0.01f))
        )
    } else if (supportsBackdropBlur()) {
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.03f))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.40f), Color.White.copy(alpha = 0.08f))
        )
    }

    // A white hairline reads as a highlight against a dark surface, but is
    // invisible against Finlog's near-white page background in light mode - a
    // faint dark line does the same "catching an edge" job there instead.
    val edge = if (isDark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.08f)

    Box(
        modifier = modifier
            // Blur or tint alone doesn't read as "floating" over a flat
            // background with nothing behind it to distort - the drop shadow is
            // what actually sells the glass card sitting above the content.
            .shadow(elevation = 16.dp, shape = shape, clip = false)
            .clip(shape)
    ) {
        // matchParentSize, never fillMaxSize: these decorations must size to the
        // content and must not influence it. fillMaxSize made the surface expand
        // to the incoming max constraints, which let the bar swallow the screen.
        BackdropBlur(modifier = Modifier.matchParentSize(), isDark = isDark)

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(base.copy(alpha = baseAlpha))
                .background(sheen)
                .border(width = 1.dp, color = edge, shape = shape)
        )

        content()
    }
}
