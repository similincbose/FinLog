package me.riafy.finlog.ui.expensedetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.data.repo.ExpenseRepository

class ExpenseDetailViewModel(
    private val expenseId: Long,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    data class ExpenseDetailUiState(
        val isLoading: Boolean = true,
        val expense: Expense? = null,
        val isDeleted: Boolean = false
    )

    var uiState = mutableStateOf(ExpenseDetailUiState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val expense = expenseRepository.getById(expenseId)
            uiState.value = uiState.value.copy(isLoading = false, expense = expense)
        }
    }

    fun delete() {
        viewModelScope.launch {
            expenseRepository.delete(expenseId)
            uiState.value = uiState.value.copy(isDeleted = true)
        }
    }
}
