package me.riafy.finlog.ui.managecategories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.data.models.DefaultCategories
import me.riafy.finlog.ui.components.CategoryIcon
import me.riafy.finlog.ui.components.EmptyState
import me.riafy.finlog.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(
    viewModel: ManageCategoriesViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState
    var pendingDelete by remember { mutableStateOf<Category?>(null) }

    // Add/Edit Category is a separate route with its own ViewModel, so this
    // screen's list needs to reload when it resumes rather than only on init.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.load()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Filled.Add, contentDescription = "Add category")
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

        if (state.categories.isEmpty()) {
            EmptyState(
                title = "No categories yet",
                message = "Add a category to start organizing your expenses.",
                modifier = Modifier.padding(padding),
                primaryActionLabel = "Add category",
                onPrimaryAction = onAddClick
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(state.categories, key = { it.id }) { category ->
                val isFallback = category.name == DefaultCategories.FALLBACK_NAME
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditClick(category.id) }
                        .padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIcon(category = category)
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { pendingDelete = category },
                        enabled = !isFallback
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete ${category.name}",
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

    pendingDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete ${category.name}?") },
            text = { Text("Expenses under this category will be moved to \"Other\".") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(category.id)
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}
