package me.riafy.finlog.ui.expensedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.ui.components.CategoryIcon
import me.riafy.finlog.ui.theme.MoneyDisplayStyle
import me.riafy.finlog.ui.theme.Spacing
import me.riafy.finlog.utils.date.asDayMonthYear
import me.riafy.finlog.utils.money.MoneyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    viewModel: ExpenseDetailViewModel,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onBackClick()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Expense") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    state.expense?.let { expense ->
                        IconButton(onClick = { onEditClick(expense.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        val expense = state.expense

        if (state.isLoading || expense == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                CategoryIcon(category = expense.category, size = 56.dp)
                Text(
                    text = expense.merchant?.takeIf { it.isNotBlank() } ?: expense.category.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = MoneyFormatter.format(expense.amount),
                    style = MoneyDisplayStyle
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                DetailRow(label = "Category", value = expense.category.name)
                DetailRow(label = "Payment method", value = expense.paymentMethod.name)
                DetailRow(label = "Date", value = expense.date.asDayMonthYear())
                if (!expense.notes.isNullOrBlank()) {
                    DetailRow(label = "Notes", value = expense.notes)
                }
                if (expense.subtotal != null) {
                    DetailRow(label = "Subtotal", value = MoneyFormatter.format(expense.subtotal))
                }
                if (expense.tax != null) {
                    DetailRow(label = "Tax", value = MoneyFormatter.format(expense.tax))
                }
                if (expense.discount != null) {
                    DetailRow(label = "Discount", value = MoneyFormatter.format(expense.discount))
                }
                if (expense.receiptNumber != null) {
                    DetailRow(label = "Receipt no.", value = expense.receiptNumber)
                }
            }

            if (expense.lineItems.isNotEmpty()) {
                HorizontalDivider()
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(text = "Items", style = MaterialTheme.typography.titleMedium)
                    expense.lineItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.name, style = MaterialTheme.typography.bodyMedium)
                            if (item.total != null) {
                                Text(text = MoneyFormatter.format(item.total), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete expense?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
