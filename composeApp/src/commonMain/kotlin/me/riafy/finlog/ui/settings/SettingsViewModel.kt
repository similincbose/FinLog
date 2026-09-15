package me.riafy.finlog.ui.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.riafy.finlog.data.local.preference.AppPreference
import me.riafy.finlog.data.repo.ExpenseRepository
import me.riafy.finlog.utils.export.CsvExporter
import me.riafy.finlog.utils.share.ShareService

class SettingsViewModel(
    val prefs: AppPreference,
    private val expenseRepository: ExpenseRepository,
    private val shareService: ShareService
) : ViewModel() {

    data class SettingsUiState(
        val themeMode: String,
        val currencyCode: String,
        val isExporting: Boolean = false
    )

    var uiState = mutableStateOf(SettingsUiState(themeMode = prefs.themeMode, currencyCode = prefs.currencyCode))
        private set

    fun setThemeMode(mode: String) {
        prefs.themeMode = mode
        uiState.value = uiState.value.copy(themeMode = mode)
    }

    fun setCurrencyCode(code: String) {
        prefs.currencyCode = code
        uiState.value = uiState.value.copy(currencyCode = code)
    }

    fun exportCsv() {
        if (uiState.value.isExporting) return
        uiState.value = uiState.value.copy(isExporting = true)

        viewModelScope.launch {
            val expenses = expenseRepository.getFiltered()
            val csv = CsvExporter.build(expenses)
            shareService.share(csv, "finlog_expenses.csv")
            uiState.value = uiState.value.copy(isExporting = false)
        }
    }
}
