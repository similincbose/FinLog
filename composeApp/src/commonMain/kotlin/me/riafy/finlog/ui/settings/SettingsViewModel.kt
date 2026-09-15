package me.riafy.finlog.ui.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import me.riafy.finlog.data.local.preference.AppPreference

class SettingsViewModel(val prefs: AppPreference) : ViewModel() {

    data class SettingsUiState(
        val themeMode: String,
        val currencyCode: String
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
}
