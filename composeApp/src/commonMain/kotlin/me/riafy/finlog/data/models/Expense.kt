package me.riafy.finlog.data.models

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class Expense(
    val id: Long,
    val amount: Money,
    val category: Category,
    val paymentMethod: PaymentMethod,
    val merchant: String?,
    val notes: String?,
    val date: LocalDate,
    val receiptImagePath: String?,
    val subtotal: Money?,
    val tax: Money?,
    val discount: Money?,
    val receiptNumber: String?,
    /** Set when this expense came from a receipt scan whose extraction looked uncertain. */
    val needsReview: Boolean,
    val lineItems: List<LineItem>,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class LineItem(
    val id: Long,
    val name: String,
    val quantity: Double?,
    val unitPrice: Money?,
    val total: Money?
)

/**
 * What the Add/Edit Expense and Receipt Review screens hand to the repository.
 * A plain parameter list would run past a dozen positional values here, so this
 * groups them the way the screens themselves think about a draft expense.
 */
data class ExpenseInput(
    val amount: Money,
    val categoryId: Long,
    val paymentMethodId: Long,
    val merchant: String?,
    val notes: String?,
    val date: LocalDate,
    val receiptImagePath: String? = null,
    val subtotal: Money? = null,
    val tax: Money? = null,
    val discount: Money? = null,
    val receiptNumber: String? = null,
    val needsReview: Boolean = false,
    val lineItems: List<LineItemInput> = emptyList()
)

data class LineItemInput(
    val name: String,
    val quantity: Double?,
    val unitPrice: Money?,
    val total: Money?
)
