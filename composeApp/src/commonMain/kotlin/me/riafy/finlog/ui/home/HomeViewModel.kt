package me.riafy.finlog.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import me.riafy.finlog.data.local.preference.AppPreference
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.data.models.Money
import me.riafy.finlog.data.repo.CategoryRepository
import me.riafy.finlog.data.repo.ExpenseRepository
import me.riafy.finlog.data.repo.PaymentMethodRepository

class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    val prefs: AppPreference
) : ViewModel() {

    companion object {
        private const val RECENT_EXPENSE_COUNT = 5L
    }

    data class HomeUiState(
        val isLoading: Boolean = true,
        val monthTotal: Money = Money.zero("INR"),
        val todayTotal: Money = Money.zero("INR"),
        val transactionCountThisMonth: Long = 0,
        val topCategory: Category? = null,
        val recentExpenses: List<Expense> = emptyList()
    )

    var uiState = mutableStateOf(HomeUiState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            // First launch: nothing to seed against yet, so the default
            // categories and payment methods need to exist before anything else runs.
            categoryRepository.seedDefaultsIfNeeded()
            paymentMethodRepository.seedDefaultsIfNeeded()

            val currency = prefs.currencyCode
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val monthStart = LocalDate(today.year, today.month, 1)

            val monthTotal = expenseRepository.getTotalBetween(monthStart, today, currency)
            val todayTotal = expenseRepository.getTotalBetween(today, today, currency)
            val transactionCount = expenseRepository.getCountBetween(monthStart, today)
            val categoryTotals = expenseRepository.getCategoryTotalsBetween(monthStart, today)
            val topCategory = categoryTotals.firstOrNull()?.let { (categoryId, _) -> categoryRepository.findById(categoryId) }
            val recent = expenseRepository.getRecent(RECENT_EXPENSE_COUNT)

            uiState.value = HomeUiState(
                isLoading = false,
                monthTotal = monthTotal,
                todayTotal = todayTotal,
                transactionCountThisMonth = transactionCount,
                topCategory = topCategory,
                recentExpenses = recent
            )
        }
    }
}
