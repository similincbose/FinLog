package me.riafy.finlog.ui.receiptreview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.riafy.finlog.data.models.ReceiptField
import me.riafy.finlog.ui.components.CategoryIcon
import me.riafy.finlog.ui.components.FilterChip
import me.riafy.finlog.ui.components.MoneyInput
import me.riafy.finlog.ui.theme.Spacing
import me.riafy.finlog.utils.date.asDayMonthYear
import me.riafy.finlog.utils.date.toEpochMillisUtc
import me.riafy.finlog.utils.date.toLocalDateFromEpochMillisUtc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptReviewScreen(
    viewModel: ReceiptReviewViewModel,
    currencyCode: String,
    onSaved: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState

    LaunchedEffect(state.savedExpenseId) {
        state.savedExpenseId?.let(onSaved)
    }
    LaunchedEffect(state.nothingToReview) {
        if (state.nothingToReview) onBackClick()
    }

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Review Receipt") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MoneyInput(
                    text = state.amountText,
                    onTextChange = viewModel::onAmountChange,
                    currencyCode = currencyCode
                )
                if (state.amountError != null) {
                    Text(
                        text = state.amountError.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (ReceiptField.TOTAL in state.uncertainFields) {
                    UncertainNote(text = "Please verify the total")
                }
            }

            OutlinedTextField(
                value = state.merchant,
                onValueChange = viewModel::onMerchantChange,
                label = { FieldLabel("Merchant", ReceiptField.MERCHANT in state.uncertainFields) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Category", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    items(state.categories, key = { it.id }) { category ->
                        FilterChip(
                            label = category.name,
                            isSelected = state.selectedCategoryId == category.id,
                            onClick = { viewModel.onCategorySelected(category.id) }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Payment method", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    items(state.paymentMethods, key = { it.id }) { method ->
                        FilterChip(
                            label = method.name,
                            isSelected = state.selectedPaymentMethodId == method.id,
                            onClick = { viewModel.onPaymentMethodSelected(method.id) }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FieldLabel("Date", ReceiptField.DATE in state.uncertainFields)
                    TextButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(text = "  " + state.date.asDayMonthYear())
                    }
                }
            }

            HorizontalDivider()

            OutlinedTextField(
                value = state.subtotalText,
                onValueChange = viewModel::onSubtotalChange,
                label = { Text("Subtotal") },
                placeholder = { Text("Optional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.taxText,
                onValueChange = viewModel::onTaxChange,
                label = { Text("Tax") },
                placeholder = { Text("Optional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.discountText,
                onValueChange = viewModel::onDiscountChange,
                label = { Text("Discount") },
                placeholder = { Text("Optional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.receiptNumberText,
                onValueChange = viewModel::onReceiptNumberChange,
                label = { Text("Receipt no.") },
                placeholder = { Text("Optional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
                placeholder = { Text("Optional") },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.lineItems.isNotEmpty()) {
                HorizontalDivider()
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(text = "Items", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Some items couldn't be recognized reliably - remove anything that looks wrong.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    state.lineItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { viewModel.onLineItemNameChange(index, it) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.totalText,
                                onValueChange = { viewModel.onLineItemTotalChange(index, it) },
                                singleLine = true,
                                modifier = Modifier.weight(0.6f)
                            )
                            IconButton(onClick = { viewModel.onRemoveLineItem(index) }) {
                                Icon(Icons.Filled.Close, contentDescription = "Remove item")
                            }
                        }
                    }
                }
            }

            Button(
                onClick = viewModel::save,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Saving…" else "Save Expense")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.date.toEpochMillisUtc()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis -> viewModel.onDateChange(millis.toLocalDateFromEpochMillisUtc()) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FieldLabel(text: String, isUncertain: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
        Text(text)
        if (isUncertain) {
            Icon(
                imageVector = Icons.Filled.WarningAmber,
                contentDescription = "Please verify",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun UncertainNote(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
        Icon(
            imageVector = Icons.Filled.WarningAmber,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(16.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
