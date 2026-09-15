package me.riafy.finlog.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import me.riafy.finlog.ui.addexpense.AddExpenseScreen
import me.riafy.finlog.ui.addexpense.AddExpenseViewModel
import me.riafy.finlog.ui.expensedetail.ExpenseDetailScreen
import me.riafy.finlog.ui.expensedetail.ExpenseDetailViewModel
import me.riafy.finlog.ui.home.HomeScreen
import me.riafy.finlog.ui.home.HomeViewModel
import me.riafy.finlog.ui.insights.InsightsScreen
import me.riafy.finlog.ui.insights.InsightsViewModel
import me.riafy.finlog.ui.settings.SettingsScreen
import me.riafy.finlog.ui.settings.SettingsViewModel
import me.riafy.finlog.ui.transactions.TransactionsScreen
import me.riafy.finlog.ui.transactions.TransactionsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: FinlogRoutes.HOME

    val isTabRoute = FinlogTab.entries.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (isTabRoute) {
                NavigationBar {
                    FinlogTab.entries.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(tab.route) {
                                        popUpTo(FinlogRoutes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.icon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) },
                            // The M3 default indicator is secondaryContainer, which we
                            // never assign a brand meaning to - use primary instead.
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = FinlogRoutes.HOME,
            // Zero automatically on non-tab routes, since the bar itself renders nothing there.
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
        ) {
            composable(FinlogRoutes.HOME) {
                val viewModel = koinViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    onAddExpenseClick = { navController.navigate(FinlogRoutes.ADD_EXPENSE) },
                    onScanReceiptClick = { navController.navigate(FinlogRoutes.ADD_EXPENSE) },
                    onSeeAllTransactionsClick = {
                        navController.navigate(FinlogRoutes.TRANSACTIONS) {
                            popUpTo(FinlogRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onExpenseClick = { id -> navController.navigate(FinlogRoutes.expenseDetail(id)) }
                )
            }

            composable(FinlogRoutes.TRANSACTIONS) {
                val viewModel = koinViewModel<TransactionsViewModel>()
                TransactionsScreen(
                    viewModel = viewModel,
                    onExpenseClick = { id -> navController.navigate(FinlogRoutes.expenseDetail(id)) }
                )
            }

            composable(FinlogRoutes.INSIGHTS) {
                val viewModel = koinViewModel<InsightsViewModel>()
                InsightsScreen(viewModel = viewModel)
            }

            composable(FinlogRoutes.SETTINGS) {
                SettingsScreen(viewModel = settingsViewModel)
            }

            composable(FinlogRoutes.ADD_EXPENSE) {
                val viewModel = koinViewModel<AddExpenseViewModel> { parametersOf(null) }
                val settingsState by settingsViewModel.uiState
                AddExpenseScreen(
                    viewModel = viewModel,
                    currencyCode = settingsState.currencyCode,
                    onSaved = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = FinlogRoutes.EDIT_EXPENSE,
                arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
            ) { entry ->
                val expenseId = entry.arguments?.read { getLong("expenseId") } ?: return@composable
                val viewModel = koinViewModel<AddExpenseViewModel> { parametersOf(expenseId) }
                val settingsState by settingsViewModel.uiState
                AddExpenseScreen(
                    viewModel = viewModel,
                    currencyCode = settingsState.currencyCode,
                    onSaved = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = FinlogRoutes.EXPENSE_DETAIL,
                arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
            ) { entry ->
                val expenseId = entry.arguments?.read { getLong("expenseId") } ?: return@composable
                val viewModel = koinViewModel<ExpenseDetailViewModel> { parametersOf(expenseId) }
                ExpenseDetailScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { id ->
                        navController.navigate(FinlogRoutes.editExpense(id))
                    }
                )
            }
        }
    }
}
