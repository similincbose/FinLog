package me.riafy.finlog.ui.managepaymentmethods

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.riafy.finlog.data.models.DefaultPaymentMethods
import me.riafy.finlog.data.models.PaymentMethod
import me.riafy.finlog.ui.components.EmptyState
import me.riafy.finlog.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePaymentMethodsScreen(
    viewModel: ManagePaymentMethodsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState
    var pendingDelete by remember { mutableStateOf<PaymentMethod?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Payment methods") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::openAddEditor) {
                        Icon(Icons.Filled.Add, contentDescription = "Add payment method")
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

        if (state.paymentMethods.isEmpty()) {
            EmptyState(
                title = "No payment methods yet",
                message = "Add a payment method to start tracking how you pay.",
                modifier = Modifier.padding(padding),
                primaryActionLabel = "Add payment method",
                onPrimaryAction = viewModel::openAddEditor
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(state.paymentMethods, key = { it.id }) { method ->
                val isFallback = method.name == DefaultPaymentMethods.FALLBACK_NAME
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openEditEditor(method) }
                        .padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = method.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { pendingDelete = method },
                        enabled = !isFallback
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete ${method.name}",
                            tint = if (isFallback) {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.isEditorOpen) {
        AlertDialog(
            onDismissRequest = viewModel::closeEditor,
            title = { Text(if (state.editorId != null) "Edit payment method" else "Add payment method") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.editorName,
                        onValueChange = viewModel::onEditorNameChange,
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.editorNameError != null) {
                        Text(
                            text = state.editorNameError.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::saveEditor) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeEditor) { Text("Cancel") }
            }
        )
    }

    pendingDelete?.let { method ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete ${method.name}?") },
            text = { Text("Expenses under this payment method will be moved to \"Other\".") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(method.id)
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}
