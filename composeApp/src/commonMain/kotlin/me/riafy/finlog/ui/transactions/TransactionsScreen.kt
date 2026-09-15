package me.riafy.finlog.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.ui.components.EmptyState
import me.riafy.finlog.ui.components.ExpenseRow
import me.riafy.finlog.ui.components.FilterChip
import me.riafy.finlog.ui.theme.Spacing
import me.riafy.finlog.utils.date.asRelativeLabel

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState

    Column(modifier = modifier.fillMaxSize().statusBarsPadding()) {
        Text(
            text = "Transactions",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.md)
        )

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            placeholder = { Text("Search merchant or notes") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md)
        )

        if (state.categories.isNotEmpty() || state.paymentMethods.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                items(state.categories, key = { "category-${it.id}" }) { category ->
                    FilterChip(
                        label = category.name,
                        isSelected = state.selectedCategoryId == category.id,
                        onClick = { viewModel.onCategoryFilterSelected(category.id) }
                    )
                }
                items(state.paymentMethods, key = { "payment-${it.id}" }) { method ->
                    FilterChip(
                        label = method.name,
                        isSelected = state.selectedPaymentMethodId == method.id,
                        onClick = { viewModel.onPaymentMethodFilterSelected(method.id) }
                    )
                }
            }
        }

        if (!state.isLoading && state.expenses.isEmpty()) {
            EmptyState(
                title = if (state.hasActiveFilters || state.searchQuery.isNotBlank()) "No matches" else "No expenses yet",
                message = if (state.hasActiveFilters || state.searchQuery.isNotBlank())
                    "No expenses match your filters."
                else
                    "Your spending will appear here once you add your first expense.",
                modifier = Modifier.fillMaxSize()
            )
            return@Column
        }

        val grouped = state.expenses.groupBy { it.date }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = Spacing.md, end = Spacing.md, top = Spacing.xs, bottom = Spacing.bottomBarClearance)
        ) {
            grouped.forEach { (date, expensesForDate) ->
                item(key = "header-$date") {
                    Text(
                        text = date.asRelativeLabel(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.xxs)
                    )
                }
                items(expensesForDate, key = { it.id }) { expense: Expense ->
                    ExpenseRow(expense = expense, onClick = { onExpenseClick(expense.id) })
                }
            }
        }
    }
}
