package me.riafy.finlog.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import me.riafy.finlog.data.local.preference.AppPreference
import me.riafy.finlog.data.models.CurrencyInfo
import me.riafy.finlog.ui.components.FilterChip
import me.riafy.finlog.ui.theme.Spacing

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onCategoriesClick: () -> Unit,
    onPaymentMethodsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState

    LazyColumn(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(start = Spacing.md, end = Spacing.md, top = Spacing.md, bottom = Spacing.bottomBarClearance),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item {
            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Appearance", style = MaterialTheme.typography.labelLarge)
                val options = listOf(
                    AppPreference.THEME_SYSTEM to "System",
                    AppPreference.THEME_LIGHT to "Light",
                    AppPreference.THEME_DARK to "Dark"
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    options.forEachIndexed { index, (mode, label) ->
                        SegmentedButton(
                            selected = state.themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Currency", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    items(CurrencyInfo.supported, key = { it.code }) { entry ->
                        FilterChip(
                            label = "${entry.symbol} ${entry.code}",
                            isSelected = state.currencyCode == entry.code,
                            onClick = { viewModel.setCurrencyCode(entry.code) }
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Manage", style = MaterialTheme.typography.labelLarge)
                SettingsRow(icon = Icons.Filled.Category, label = "Categories", onClick = onCategoriesClick)
                SettingsRow(icon = Icons.Filled.Payments, label = "Payment methods", onClick = onPaymentMethodsClick)
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(text = "Data", style = MaterialTheme.typography.labelLarge)
                SettingsRow(
                    icon = Icons.Filled.FileDownload,
                    label = if (state.isExporting) "Exporting…" else "Export as CSV",
                    onClick = viewModel::exportCsv,
                    trailing = { if (state.isExporting) CircularProgressIndicator(modifier = Modifier.size(20.dp)) }
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "Privacy", style = MaterialTheme.typography.labelLarge)
                }
                Text(
                    text = "Your financial data is stored locally on this device. Nothing is uploaded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(text = "About", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = "Finlog 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** A tappable settings entry - leading icon, label, and a trailing chevron unless a custom trailing slot is given. */
@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
