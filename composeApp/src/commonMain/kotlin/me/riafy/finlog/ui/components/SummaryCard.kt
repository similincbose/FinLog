package me.riafy.finlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import me.riafy.finlog.ui.theme.MoneyDisplayStyle
import me.riafy.finlog.ui.theme.Shapes
import me.riafy.finlog.ui.theme.Spacing

/** The large "Spent this month" figure at the top of the dashboard. */
@Composable
fun HeroSummaryCard(
    label: String,
    amountText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(Spacing.lg)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = amountText,
            style = MoneyDisplayStyle,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/** A smaller stat tile - "Today", "Transactions", "Top category" etc. */
@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(Shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(Spacing.md)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
