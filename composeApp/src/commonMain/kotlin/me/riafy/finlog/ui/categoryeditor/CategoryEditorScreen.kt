package me.riafy.finlog.ui.categoryeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.riafy.finlog.data.models.CategoryColorPalette
import me.riafy.finlog.ui.components.CategoryIcons
import me.riafy.finlog.ui.theme.Spacing
import me.riafy.finlog.utils.parseHexColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryEditorScreen(
    viewModel: CategoryEditorViewModel,
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Edit Category" else "Add Category") },
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
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.nameError != null) {
                    Text(
                        text = state.nameError.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Icon", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryIcons.assignable.forEach { (key, icon) ->
                        val isSelected = state.iconKey == key
                        val tint = parseHexColor(state.colorHex)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) tint.copy(alpha = 0.20f) else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.onIconSelected(key) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = key,
                                tint = if (isSelected) tint else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Color", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryColorPalette.hexColors.forEach { hex ->
                        val color = parseHexColor(hex)
                        val isSelected = state.colorHex == hex
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { viewModel.onColorSelected(hex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White
                                )
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
                Text(if (state.isSaving) "Saving…" else "Save")
            }
        }
    }
}
