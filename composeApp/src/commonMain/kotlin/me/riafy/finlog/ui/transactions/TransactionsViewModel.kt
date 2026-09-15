package me.riafy.finlog.ui.transactions

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.data.models.PaymentMethod
import me.riafy.finlog.data.repo.CategoryRepository
import me.riafy.finlog.data.repo.ExpenseRepository
import me.riafy.finlog.data.repo.PaymentMethodRepository

class TransactionsViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {

    data class TransactionsUiState(
        val isLoading: Boolean = true,
        val expenses: List<Expense> = emptyList(),
        val categories: List<Category> = emptyList(),
        val paymentMethods: List<PaymentMethod> = emptyList(),
        val searchQuery: String = "",
        val selectedCategoryId: Long? = null,
        val selectedPaymentMethodId: Long? = null
    ) {
        val hasActiveFilters: Boolean get() = selectedCategoryId != null || selectedPaymentMethodId != null
    }

    var uiState = mutableStateOf(TransactionsUiState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val categories = categoryRepository.getAll()
            val paymentMethods = paymentMethodRepository.getAll()
            uiState.value = uiState.value.copy(categories = categories, paymentMethods = paymentMethods)
            refresh()
        }
    }

    fun onSearchQueryChange(query: String) {
        uiState.value = uiState.value.copy(searchQuery = query)
        refresh()
    }

    fun onCategoryFilterSelected(categoryId: Long?) {
        val current = uiState.value.selectedCategoryId
        uiState.value = uiState.value.copy(selectedCategoryId = if (current == categoryId) null else categoryId)
        refresh()
    }

    fun onPaymentMethodFilterSelected(paymentMethodId: Long?) {
        val current = uiState.value.selectedPaymentMethodId
        uiState.value = uiState.value.copy(
            selectedPaymentMethodId = if (current == paymentMethodId) null else paymentMethodId
        )
        refresh()
    }

    fun clearFilters() {
        uiState.value = uiState.value.copy(selectedCategoryId = null, selectedPaymentMethodId = null, searchQuery = "")
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val state = uiState.value
            val expenses = expenseRepository.getFiltered(
                searchQuery = state.searchQuery,
                categoryId = state.selectedCategoryId,
                paymentMethodId = state.selectedPaymentMethodId
            )
            uiState.value = uiState.value.copy(isLoading = false, expenses = expenses)
        }
    }
}
