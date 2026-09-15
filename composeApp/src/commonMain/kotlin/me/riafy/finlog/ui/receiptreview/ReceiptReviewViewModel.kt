package me.riafy.finlog.ui.receiptreview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import me.riafy.finlog.data.local.preference.AppPreference
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.data.models.ExpenseInput
import me.riafy.finlog.data.models.LineItemInput
import me.riafy.finlog.data.models.Money
import me.riafy.finlog.data.models.ParsedReceipt
import me.riafy.finlog.data.models.PaymentMethod
import me.riafy.finlog.data.models.ReceiptField
import me.riafy.finlog.data.repo.CategoryRepository
import me.riafy.finlog.data.repo.ExpenseRepository
import me.riafy.finlog.data.repo.PaymentMethodRepository
import me.riafy.finlog.utils.money.MoneyFormatter
import me.riafy.finlog.utils.receipt.PendingReceiptHolder
import kotlin.time.Clock

data class EditableLineItem(val name: String, val totalText: String)

class ReceiptReviewViewModel(
    private val pendingReceiptHolder: PendingReceiptHolder,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val prefs: AppPreference
) : ViewModel() {

    data class ReceiptReviewUiState(
        val isLoading: Boolean = true,
        /** True only if there was nothing to review - e.g. the process was killed between scan and review. */
        val nothingToReview: Boolean = false,
        val imagePath: String = "",
        val categories: List<Category> = emptyList(),
        val paymentMethods: List<PaymentMethod> = emptyList(),
        val merchant: String = "",
        val amountText: String = "",
        val selectedCategoryId: Long? = null,
        val selectedPaymentMethodId: Long? = null,
        val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        val notes: String = "",
        val subtotalText: String = "",
        val taxText: String = "",
        val discountText: String = "",
        val receiptNumberText: String = "",
        val lineItems: List<EditableLineItem> = emptyList(),
        val uncertainFields: Set<ReceiptField> = emptySet(),
        val amountError: String? = null,
        val isSaving: Boolean = false,
        val savedExpenseId: Long? = null
    )

    var uiState = mutableStateOf(ReceiptReviewUiState())
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val scanned = pendingReceiptHolder.take()
            if (scanned == null) {
                uiState.value = uiState.value.copy(isLoading = false, nothingToReview = true)
                return@launch
            }

            val categories = categoryRepository.getAll()
            val paymentMethods = paymentMethodRepository.getAll()
            val parsed = scanned.parsed

            uiState.value = ReceiptReviewUiState(
                isLoading = false,
                imagePath = scanned.imagePath,
                categories = categories,
                paymentMethods = paymentMethods,
                merchant = parsed.merchant.orEmpty(),
                amountText = parsed.total?.let { MoneyFormatter.formatForInput(it) }.orEmpty(),
                selectedCategoryId = guessCategoryId(parsed, categories),
                selectedPaymentMethodId = paymentMethods.find { it.name == parsed.paymentMethodGuess }?.id
                    ?: paymentMethods.firstOrNull()?.id,
                date = parsed.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault()),
                subtotalText = parsed.subtotal?.let { MoneyFormatter.formatForInput(it) }.orEmpty(),
                taxText = parsed.tax?.let { MoneyFormatter.formatForInput(it) }.orEmpty(),
                discountText = parsed.discount?.let { MoneyFormatter.formatForInput(it) }.orEmpty(),
                receiptNumberText = parsed.receiptNumber.orEmpty(),
                lineItems = parsed.lineItems.map { item ->
                    EditableLineItem(name = item.name, totalText = item.total?.let { MoneyFormatter.formatForInput(it) }.orEmpty())
                },
                uncertainFields = parsed.uncertainFields
            )
        }
    }

    /** Merchant-name keyword match against the default category set - a starting guess, never a confident final answer. */
    private fun guessCategoryId(parsed: ParsedReceipt, categories: List<Category>): Long? {
        val merchant = parsed.merchant?.lowercase() ?: return null
        val keywordsByCategoryName = mapOf(
            "Food" to listOf("restaurant", "cafe", "food", "kitchen", "hub", "diner", "eatery", "hotel"),
            "Transport" to listOf("uber", "ola", "taxi", "cab", "fuel", "petrol", "metro"),
            "Shopping" to listOf("mart", "store", "shop", "supermarket", "mall"),
            "Bills" to listOf("electricity", "utility", "broadband", "recharge"),
            "Health" to listOf("pharmacy", "hospital", "clinic", "medical", "chemist"),
            "Entertainment" to listOf("cinema", "movies", "theatre")
        )
        val matchedName = keywordsByCategoryName.entries.firstOrNull { (_, keywords) ->
            keywords.any { merchant.contains(it) }
        }?.key
        return categories.find { it.name == matchedName }?.id
    }

    fun onMerchantChange(value: String) {
        uiState.value = uiState.value.copy(merchant = value)
    }

    fun onAmountChange(value: String) {
        uiState.value = uiState.value.copy(amountText = value, amountError = null)
    }

    fun onCategorySelected(categoryId: Long) {
        uiState.value = uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun onPaymentMethodSelected(paymentMethodId: Long) {
        uiState.value = uiState.value.copy(selectedPaymentMethodId = paymentMethodId)
    }

    fun onDateChange(date: LocalDate) {
        uiState.value = uiState.value.copy(date = date)
    }

    fun onNotesChange(value: String) {
        uiState.value = uiState.value.copy(notes = value)
    }

    fun onSubtotalChange(value: String) {
        uiState.value = uiState.value.copy(subtotalText = value)
    }

    fun onTaxChange(value: String) {
        uiState.value = uiState.value.copy(taxText = value)
    }

    fun onDiscountChange(value: String) {
        uiState.value = uiState.value.copy(discountText = value)
    }

    fun onReceiptNumberChange(value: String) {
        uiState.value = uiState.value.copy(receiptNumberText = value)
    }

    fun onLineItemNameChange(index: Int, value: String) {
        val items = uiState.value.lineItems.toMutableList()
        items[index] = items[index].copy(name = value)
        uiState.value = uiState.value.copy(lineItems = items)
    }

    fun onLineItemTotalChange(index: Int, value: String) {
        val items = uiState.value.lineItems.toMutableList()
        items[index] = items[index].copy(totalText = value)
        uiState.value = uiState.value.copy(lineItems = items)
    }

    fun onRemoveLineItem(index: Int) {
        val items = uiState.value.lineItems.toMutableList()
        items.removeAt(index)
        uiState.value = uiState.value.copy(lineItems = items)
    }

    fun save() {
        val state = uiState.value
        val currency = prefs.currencyCode
        val amount = Money.fromInput(state.amountText, currency)

        if (amount == null || amount.isZero) {
            uiState.value = state.copy(amountError = "Enter a valid amount")
            return
        }
        val categoryId = state.selectedCategoryId ?: state.categories.firstOrNull()?.id
        val paymentMethodId = state.selectedPaymentMethodId ?: state.paymentMethods.firstOrNull()?.id
        if (categoryId == null || paymentMethodId == null) return

        uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            val input = ExpenseInput(
                amount = amount,
                categoryId = categoryId,
                paymentMethodId = paymentMethodId,
                merchant = state.merchant,
                notes = state.notes,
                date = state.date,
                receiptImagePath = state.imagePath,
                subtotal = state.subtotalText.takeIf { it.isNotBlank() }?.let { Money.fromInput(it, currency) },
                tax = state.taxText.takeIf { it.isNotBlank() }?.let { Money.fromInput(it, currency) },
                discount = state.discountText.takeIf { it.isNotBlank() }?.let { Money.fromInput(it, currency) },
                receiptNumber = state.receiptNumberText.takeIf { it.isNotBlank() },
                needsReview = state.uncertainFields.isNotEmpty(),
                lineItems = state.lineItems.mapNotNull { item ->
                    val total = Money.fromInput(item.totalText, currency) ?: return@mapNotNull null
                    if (item.name.isBlank()) return@mapNotNull null
                    LineItemInput(name = item.name, quantity = null, unitPrice = null, total = total)
                }
            )

            val id = expenseRepository.add(input)
            uiState.value = uiState.value.copy(isSaving = false, savedExpenseId = id)
        }
    }
}
