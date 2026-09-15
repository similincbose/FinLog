package me.riafy.finlog.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.riafy.finlog.ui.components.EmptyState
import me.riafy.finlog.ui.components.ExpenseRow
import me.riafy.finlog.ui.components.HeroSummaryCard
import me.riafy.finlog.ui.components.SectionHeader
import me.riafy.finlog.ui.components.StatTile
import me.riafy.finlog.ui.theme.Spacing
import me.riafy.finlog.utils.money.MoneyFormatter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddExpenseClick: () -> Unit,
    onScanReceiptClick: () -> Unit,
    onSeeAllTransactionsClick: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState
    val screenModifier = modifier.fillMaxSize().statusBarsPadding()

    if (state.isLoading) {
        Column(
            modifier = screenModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = screenModifier,
        contentPadding = PaddingValues(
            start = Spacing.md,
            end = Spacing.md,
            top = Spacing.md,
            bottom = Spacing.bottomBarClearance
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item {
            Text(text = "Finlog", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            HeroSummaryCard(
                label = "Spent this month",
                amountText = MoneyFormatter.format(state.monthTotal)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                StatTile(
                    label = "Today",
                    value = MoneyFormatter.format(state.todayTotal),
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Top category",
                    value = state.topCategory?.name ?: "—",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Transactions",
                    value = state.transactionCountThisMonth.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Button(
                    onClick = onAddExpenseClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Add Expense")
                }
                OutlinedButton(
                    onClick = onScanReceiptClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.DocumentScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Scan Receipt")
                }
            }
        }

        item {
            SectionHeader(
                title = "Recent",
                actionLabel = if (state.recentExpenses.isNotEmpty()) "See all" else null,
                onActionClick = if (state.recentExpenses.isNotEmpty()) onSeeAllTransactionsClick else null
            )
        }

        if (state.recentExpenses.isEmpty()) {
            item {
                EmptyState(
                    title = "No expenses yet",
                    message = "Your spending will appear here once you add your first expense.",
                    primaryActionLabel = "Add Expense",
                    onPrimaryAction = onAddExpenseClick,
                    secondaryActionLabel = "Scan Receipt",
                    onSecondaryAction = onScanReceiptClick
                )
            }
        } else {
            items(state.recentExpenses, key = { it.id }) { expense ->
                ExpenseRow(expense = expense, onClick = { onExpenseClick(expense.id) })
            }
        }
    }
}
