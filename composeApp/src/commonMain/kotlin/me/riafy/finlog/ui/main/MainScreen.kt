package me.riafy.finlog.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import me.riafy.finlog.ui.addexpense.AddExpenseScreen
import me.riafy.finlog.ui.addexpense.AddExpenseViewModel
import me.riafy.finlog.ui.categoryeditor.CategoryEditorScreen
import me.riafy.finlog.ui.categoryeditor.CategoryEditorViewModel
import me.riafy.finlog.ui.components.GlassSurface
import me.riafy.finlog.ui.expensedetail.ExpenseDetailScreen
import me.riafy.finlog.ui.expensedetail.ExpenseDetailViewModel
import me.riafy.finlog.ui.home.HomeScreen
import me.riafy.finlog.ui.home.HomeViewModel
import me.riafy.finlog.ui.insights.InsightsScreen
import me.riafy.finlog.ui.insights.InsightsViewModel
import me.riafy.finlog.ui.managecategories.ManageCategoriesScreen
import me.riafy.finlog.ui.managecategories.ManageCategoriesViewModel
import me.riafy.finlog.ui.managepaymentmethods.ManagePaymentMethodsScreen
import me.riafy.finlog.ui.managepaymentmethods.ManagePaymentMethodsViewModel
import me.riafy.finlog.ui.receiptreview.ReceiptReviewScreen
import me.riafy.finlog.ui.receiptreview.ReceiptReviewViewModel
import me.riafy.finlog.ui.receiptscan.ScanReceiptScreen
import me.riafy.finlog.ui.receiptscan.ScanReceiptViewModel
import me.riafy.finlog.ui.settings.SettingsScreen
import me.riafy.finlog.ui.settings.SettingsViewModel
import me.riafy.finlog.ui.transactions.TransactionsScreen
import me.riafy.finlog.ui.transactions.TransactionsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: FinlogRoutes.HOME

    val isTabRoute = FinlogTab.entries.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (isTabRoute) {
                FinlogBottomBar(
                    currentRoute = currentRoute,
                    isDark = isDark,
                    onTabSelected = { tab ->
                        if (tab.route != currentRoute) {
                            navController.navigate(tab.route) {
                                popUpTo(FinlogRoutes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { _ ->
        // Scaffold's padding is deliberately unused: the bar floats over the
        // content, and each screen adds bottomBarClearance so its last row
        // still clears it.
        NavHost(
            navController = navController,
            startDestination = FinlogRoutes.HOME,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(FinlogRoutes.HOME) {
                val viewModel = koinViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    onAddExpenseClick = { navController.navigate(FinlogRoutes.ADD_EXPENSE) },
                    onScanReceiptClick = { navController.navigate(FinlogRoutes.SCAN_RECEIPT) },
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
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onCategoriesClick = { navController.navigate(FinlogRoutes.CATEGORIES) },
                    onPaymentMethodsClick = { navController.navigate(FinlogRoutes.PAYMENT_METHODS) }
                )
            }

            composable(FinlogRoutes.CATEGORIES) {
                val viewModel = koinViewModel<ManageCategoriesViewModel>()
                ManageCategoriesScreen(
                    viewModel = viewModel,
                    onAddClick = { navController.navigate(FinlogRoutes.ADD_CATEGORY) },
                    onEditClick = { id -> navController.navigate(FinlogRoutes.editCategory(id)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(FinlogRoutes.ADD_CATEGORY) {
                val viewModel = koinViewModel<CategoryEditorViewModel> { parametersOf(null) }
                CategoryEditorScreen(
                    viewModel = viewModel,
                    onSaved = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = FinlogRoutes.EDIT_CATEGORY,
                arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
            ) { entry ->
                val categoryId = entry.arguments?.read { getLong("categoryId") } ?: return@composable
                val viewModel = koinViewModel<CategoryEditorViewModel> { parametersOf(categoryId) }
                CategoryEditorScreen(
                    viewModel = viewModel,
                    onSaved = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(FinlogRoutes.PAYMENT_METHODS) {
                val viewModel = koinViewModel<ManagePaymentMethodsViewModel>()
                ManagePaymentMethodsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
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

            composable(FinlogRoutes.SCAN_RECEIPT) {
                val viewModel = koinViewModel<ScanReceiptViewModel>()
                ScanReceiptScreen(
                    viewModel = viewModel,
                    onReadyToReview = {
                        navController.navigate(FinlogRoutes.RECEIPT_REVIEW) {
                            popUpTo(FinlogRoutes.SCAN_RECEIPT) { inclusive = true }
                        }
                    },
                    onEnterManuallyClick = {
                        navController.navigate(FinlogRoutes.ADD_EXPENSE) {
                            popUpTo(FinlogRoutes.SCAN_RECEIPT) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(FinlogRoutes.RECEIPT_REVIEW) {
                val viewModel = koinViewModel<ReceiptReviewViewModel>()
                val settingsState by settingsViewModel.uiState
                ReceiptReviewScreen(
                    viewModel = viewModel,
                    currencyCode = settingsState.currencyCode,
                    onSaved = { id ->
                        navController.navigate(FinlogRoutes.expenseDetail(id)) {
                            popUpTo(FinlogRoutes.HOME)
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

/**
 * A floating glass capsule rather than a full-width opaque bar - the same
 * material Netflix and Prime Video use for their iOS tab bar, and the closest
 * Android equivalent where a real backdrop blur isn't available. It sits over
 * the content instead of cutting the screen in two, which lets each screen's
 * content run to the bottom edge.
 *
 * Icon-only, like Netflix and Prime's own tab bars - no labels competing for
 * space, and every tab gets an equal, fixed-width slot instead of sizing to its
 * own content.
 */
@Composable
private fun FinlogBottomBar(
    currentRoute: String,
    isDark: Boolean,
    onTabSelected: (FinlogTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            isDark = isDark,
            cornerRadius = 26.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FinlogTab.entries.forEach { tab ->
                    BottomBarItem(
                        tab = tab,
                        isSelected = currentRoute == tab.route,
                        isDark = isDark,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    tab: FinlogTab,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    // A colour change alone read too weakly in dark mode - white vs. 55%-alpha
    // white is only a brightness difference, not an actual colour signal. The
    // brand teal now marks "selected" the same way in both themes.
    val active = MaterialTheme.colorScheme.primary
    val inactive = if (isDark) Color.White.copy(alpha = 0.55f) else Color(0xFF667085)
    val tint by animateColorAsState(
        targetValue = if (isSelected) active else inactive,
        animationSpec = tween(220),
        label = "tabTint"
    )

    // A soft pill behind the selected tab too - the same pattern Apple's own
    // native tab bars use (seen in Fitness's activity picker) - since colour and
    // icon-fill alone still weren't reading as clearly "selected" at a glance.
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(220),
        label = "tabIndicator"
    )

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(indicatorAlpha)
                .clip(CircleShape)
                .background(active.copy(alpha = if (isDark) 0.20f else 0.14f))
        )

        Icon(
            imageVector = if (isSelected) tab.selectedIcon else tab.icon,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}
