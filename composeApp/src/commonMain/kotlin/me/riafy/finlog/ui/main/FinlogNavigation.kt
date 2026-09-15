package me.riafy.finlog.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Every destination in the app. Mirrors the flat route list of our nav graphs. */
object FinlogRoutes {
    const val HOME = "home"
    const val TRANSACTIONS = "transactions"
    const val INSIGHTS = "insights"
    const val SETTINGS = "settings"
    const val ADD_EXPENSE = "add_expense"
    const val EDIT_EXPENSE = "edit_expense/{expenseId}"
    const val EXPENSE_DETAIL = "expense_detail/{expenseId}"

    fun editExpense(expenseId: Long) = "edit_expense/$expenseId"
    fun expenseDetail(expenseId: Long) = "expense_detail/$expenseId"
}

/** The four tabs in the bottom bar. */
enum class FinlogTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME(
        route = FinlogRoutes.HOME,
        label = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    TRANSACTIONS(
        route = FinlogRoutes.TRANSACTIONS,
        label = "Transactions",
        icon = Icons.Outlined.Receipt,
        selectedIcon = Icons.Filled.Receipt
    ),
    INSIGHTS(
        route = FinlogRoutes.INSIGHTS,
        label = "Insights",
        icon = Icons.Outlined.BarChart,
        selectedIcon = Icons.Filled.BarChart
    ),
    SETTINGS(
        route = FinlogRoutes.SETTINGS,
        label = "Settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings
    )
}
