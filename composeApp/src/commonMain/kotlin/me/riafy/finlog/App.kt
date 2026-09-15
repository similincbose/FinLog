package me.riafy.finlog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import me.riafy.finlog.ui.main.MainScreen
import me.riafy.finlog.ui.settings.SettingsViewModel
import me.riafy.finlog.ui.theme.FinlogTheme
import me.riafy.finlog.ui.theme.shouldUseDarkTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * Owns the one SettingsViewModel instance for the whole app. A koinViewModel()
 * call inside a NavHost destination is scoped to that back stack entry, so the
 * theme and currency picked in Settings would only reach the Settings tab
 * itself; everything that reads them - the theme here, Add Expense's currency
 * symbol - is handed this same instance instead.
 */
@Composable
fun App() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val settingsState by settingsViewModel.uiState

    val isDark = shouldUseDarkTheme(settingsState.themeMode)

    FinlogTheme(darkTheme = isDark) {
        MainScreen(settingsViewModel = settingsViewModel, isDark = isDark)
    }
}
