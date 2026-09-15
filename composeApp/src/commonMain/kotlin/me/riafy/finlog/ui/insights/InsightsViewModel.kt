package me.riafy.finlog.ui.insights

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import me.riafy.finlog.data.local.preference.AppPreference
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.data.models.Money
import me.riafy.finlog.data.repo.CategoryRepository
import me.riafy.finlog.data.repo.ExpenseRepository
import me.riafy.finlog.utils.date.currentMonthRange
import me.riafy.finlog.utils.date.previousMonthRange

class InsightsViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val prefs: AppPreference
) : ViewModel() {

    data class CategoryBreakdown(val category: Category, val total: Money, val shareOfTotal: Float)

    data class InsightsUiState(
        val isLoading: Boolean = true,
        val monthTotal: Money = Money.zero("INR"),
        val previousMonthTotal: Money = Money.zero("INR"),
        val changeVsPreviousMonthPercent: Int? = null,
        val averageDailySpend: Money = Money.zero("INR"),
        val transactionCount: Long = 0,
        val largestTransaction: Expense? = null,
        val categoryBreakdown: List<CategoryBreakdown> = emptyList()
    )

    var uiState = mutableStateOf(InsightsUiState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val currency = prefs.currencyCode
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val thisMonth = currentMonthRange(today)
            val lastMonth = previousMonthRange(today)

            val monthTotal = expenseRepository.getTotalBetween(thisMonth.start, thisMonth.end, currency)
            val previousMonthTotal = expenseRepository.getTotalBetween(lastMonth.start, lastMonth.end, currency)
            val transactionCount = expenseRepository.getCountBetween(thisMonth.start, thisMonth.end)
            val largest = expenseRepository.getLargestBetween(thisMonth.start, thisMonth.end)
            val categoryTotals = expenseRepository.getCategoryTotalsBetween(thisMonth.start, thisMonth.end)

            val daysElapsed = thisMonth.start.daysUntilInclusive(thisMonth.end)
            val averageDaily = if (daysElapsed > 0) Money(monthTotal.minorUnits / daysElapsed, currency) else Money.zero(currency)

            val change = if (!previousMonthTotal.isZero) {
                (((monthTotal.minorUnits - previousMonthTotal.minorUnits) * 100) / previousMonthTotal.minorUnits).toInt()
            } else null

            val breakdown = categoryTotals.mapNotNull { (categoryId, total) ->
                val category = categoryRepository.findById(categoryId) ?: return@mapNotNull null
                val share = if (monthTotal.minorUnits > 0) total.toFloat() / monthTotal.minorUnits.toFloat() else 0f
                CategoryBreakdown(category = category, total = Money(total, currency), shareOfTotal = share)
            }

            uiState.value = InsightsUiState(
                isLoading = false,
                monthTotal = monthTotal,
                previousMonthTotal = previousMonthTotal,
                changeVsPreviousMonthPercent = change,
                averageDailySpend = averageDaily,
                transactionCount = transactionCount,
                largestTransaction = largest,
                categoryBreakdown = breakdown
            )
        }
    }
}

private fun LocalDate.daysUntilInclusive(other: LocalDate): Int = this.daysUntil(other) + 1
