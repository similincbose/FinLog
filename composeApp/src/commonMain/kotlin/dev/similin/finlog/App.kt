package dev.similin.finlog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import dev.similin.finlog.ui.main.MainScreen
import dev.similin.finlog.ui.settings.SettingsViewModel
import dev.similin.finlog.ui.theme.FinlogTheme
import dev.similin.finlog.ui.theme.shouldUseDarkTheme
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

    FinlogTheme(darkTheme = shouldUseDarkTheme(settingsState.themeMode)) {
        MainScreen(settingsViewModel = settingsViewModel)
    }
}
