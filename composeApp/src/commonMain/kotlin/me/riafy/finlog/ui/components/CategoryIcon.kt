package me.riafy.finlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.utils.parseHexColor

/** The tinted circular badge used to identify a category in a row, chip or detail header. */
@Composable
fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val tint = parseHexColor(category.colorHex)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = CategoryIcons.iconFor(category.iconKey),
            contentDescription = category.name,
            tint = tint,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}
