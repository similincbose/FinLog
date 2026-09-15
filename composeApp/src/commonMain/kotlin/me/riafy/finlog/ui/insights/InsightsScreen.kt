package me.riafy.finlog.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import me.riafy.finlog.ui.components.EmptyState
import me.riafy.finlog.ui.components.HeroSummaryCard
import me.riafy.finlog.ui.components.StatTile
import me.riafy.finlog.ui.theme.Shapes
import me.riafy.finlog.ui.theme.Spacing
import me.riafy.finlog.utils.parseHexColor
import me.riafy.finlog.utils.money.MoneyFormatter

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState
    val screenModifier = modifier.fillMaxSize().statusBarsPadding()

    if (state.isLoading) {
        Box(modifier = screenModifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = screenModifier,
        contentPadding = PaddingValues(start = Spacing.md, end = Spacing.md, top = Spacing.md, bottom = Spacing.bottomBarClearance),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item {
            Text(text = "Insights", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                HeroSummaryCard(
                    label = "This month",
                    amountText = MoneyFormatter.format(state.monthTotal)
                )
                val change = state.changeVsPreviousMonthPercent
                if (change != null) {
                    val direction = if (change >= 0) "up" else "down"
                    Text(
                        text = "${kotlin.math.abs(change)}% $direction vs last month",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                StatTile(
                    label = "Avg / day",
                    value = MoneyFormatter.format(state.averageDailySpend),
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Transactions",
                    value = state.transactionCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Largest",
                    value = state.largestTransaction?.let { MoneyFormatter.format(it.amount) } ?: "—",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(text = "By category", style = MaterialTheme.typography.titleMedium)
        }

        if (state.categoryBreakdown.isEmpty()) {
            item {
                EmptyState(
                    title = "Nothing to show yet",
                    message = "Category totals will appear here once you log some spending this month."
                )
            }
        } else {
            items(state.categoryBreakdown) { entry ->
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = entry.category.name, style = MaterialTheme.typography.bodyMedium)
                        Text(text = MoneyFormatter.format(entry.total), style = MaterialTheme.typography.bodyMedium)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(Shapes.extraSmall)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(entry.shareOfTotal.coerceIn(0f, 1f))
                                .height(6.dp)
                                .clip(Shapes.extraSmall)
                                .background(parseHexColor(entry.category.colorHex))
                        )
                    }
                }
            }
        }
    }
}
