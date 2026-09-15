package me.riafy.finlog.ui.addexpense

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
import me.riafy.finlog.data.models.ExpenseInput
import me.riafy.finlog.data.models.Money
import me.riafy.finlog.data.models.PaymentMethod
import me.riafy.finlog.data.repo.CategoryRepository
import me.riafy.finlog.data.repo.ExpenseRepository
import me.riafy.finlog.data.repo.PaymentMethodRepository
import me.riafy.finlog.utils.money.MoneyFormatter

/**
 * Backs both Add Expense and Edit Expense - the form is the same either way,
 * the only difference is whether [expenseId] resolves to something to preload
 * and whether saving calls insert or update.
 */
class AddExpenseViewModel(
    private val expenseId: Long?,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val prefs: AppPreference
) : ViewModel() {

    data class AddExpenseUiState(
        val isLoading: Boolean = true,
        val isEditMode: Boolean = false,
        val categories: List<Category> = emptyList(),
        val paymentMethods: List<PaymentMethod> = emptyList(),
        val amountText: String = "",
        val selectedCategoryId: Long? = null,
        val selectedPaymentMethodId: Long? = null,
        val merchant: String = "",
        val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        val notes: String = "",
        val isSaving: Boolean = false,
        val amountError: String? = null,
        val savedExpenseId: Long? = null
    )

    var uiState = mutableStateOf(AddExpenseUiState(isEditMode = expenseId != null))
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            categoryRepository.seedDefaultsIfNeeded()
            paymentMethodRepository.seedDefaultsIfNeeded()

            val categories = categoryRepository.getAll()
            val paymentMethods = paymentMethodRepository.getAll()
            val existing = expenseId?.let { expenseRepository.getById(it) }

            uiState.value = if (existing != null) {
                AddExpenseUiState(
                    isLoading = false,
                    isEditMode = true,
                    categories = categories,
                    paymentMethods = paymentMethods,
                    amountText = MoneyFormatter.formatForInput(existing.amount),
                    selectedCategoryId = existing.category.id,
                    selectedPaymentMethodId = existing.paymentMethod.id,
                    merchant = existing.merchant.orEmpty(),
                    date = existing.date,
                    notes = existing.notes.orEmpty()
                )
            } else {
                AddExpenseUiState(
                    isLoading = false,
                    isEditMode = false,
                    categories = categories,
                    paymentMethods = paymentMethods,
                    selectedCategoryId = categories.firstOrNull()?.id,
                    selectedPaymentMethodId = paymentMethods.firstOrNull()?.id
                )
            }
        }
    }

    fun onAmountChange(text: String) {
        uiState.value = uiState.value.copy(amountText = text, amountError = null)
    }

    fun onCategorySelected(categoryId: Long) {
        uiState.value = uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun onPaymentMethodSelected(paymentMethodId: Long) {
        uiState.value = uiState.value.copy(selectedPaymentMethodId = paymentMethodId)
    }

    fun onMerchantChange(value: String) {
        uiState.value = uiState.value.copy(merchant = value)
    }

    fun onDateChange(date: LocalDate) {
        uiState.value = uiState.value.copy(date = date)
    }

    fun onNotesChange(value: String) {
        uiState.value = uiState.value.copy(notes = value)
    }

    fun save() {
        val state = uiState.value
        val currency = prefs.currencyCode
        val amount = Money.fromInput(state.amountText, currency)

        if (amount == null || amount.isZero) {
            uiState.value = state.copy(amountError = "Enter a valid amount")
            return
        }
        val categoryId = state.selectedCategoryId
        val paymentMethodId = state.selectedPaymentMethodId
        if (categoryId == null || paymentMethodId == null) return

        uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            val input = ExpenseInput(
                amount = amount,
                categoryId = categoryId,
                paymentMethodId = paymentMethodId,
                merchant = state.merchant,
                notes = state.notes,
                date = state.date
            )

            val id = if (expenseId != null) {
                expenseRepository.update(expenseId, input)
                expenseId
            } else {
                expenseRepository.add(input)
            }

            uiState.value = uiState.value.copy(isSaving = false, savedExpenseId = id)
        }
    }
}
