package me.riafy.finlog.data.repo

import kotlinx.datetime.LocalDate
import me.riafy.finlog.data.local.database.toDomain
import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.data.models.ExpenseInput
import me.riafy.finlog.data.models.LineItem
import me.riafy.finlog.data.models.Money
import me.riafy.finlog.database.FinlogDatabase
import kotlin.time.Clock

class ExpenseRepository(private val database: FinlogDatabase) {

    private val expenseQueries = database.expenseQueries
    private val lineItemQueries = database.lineItemQueries

    /**
     * Transaction list query. All filters are optional - a null value in any of
     * them is a no-op in the underlying SQL rather than a separate query per
     * filter combination.
     */
    suspend fun getFiltered(
        searchQuery: String? = null,
        categoryId: Long? = null,
        paymentMethodId: Long? = null,
        dateFrom: LocalDate? = null,
        dateTo: LocalDate? = null
    ): List<Expense> = expenseQueries.selectWithDetails(
        searchQuery = searchQuery?.takeIf { it.isNotBlank() },
        categoryId = categoryId,
        paymentMethodId = paymentMethodId,
        dateFrom = dateFrom?.toString(),
        dateTo = dateTo?.toString()
    ).executeAsList().map { it.toDomain(lineItems = emptyList()) }

    suspend fun getRecent(limit: Long): List<Expense> =
        expenseQueries.selectRecent(limit).executeAsList().map { it.toDomain(lineItems = emptyList()) }

    suspend fun getById(id: Long): Expense? {
        val row = expenseQueries.selectById(id).executeAsOneOrNull() ?: return null
        val items = lineItemQueries.selectByExpenseId(id).executeAsList()
            .map { it.toDomain(currencyCode = row.expense_currency_code) }
        return row.toDomain(lineItems = items)
    }

    suspend fun add(input: ExpenseInput): Long {
        val now = Clock.System.now().toEpochMilliseconds()
        expenseQueries.insert(
            amount_minor_units = input.amount.minorUnits,
            currency_code = input.amount.currencyCode,
            category_id = input.categoryId,
            payment_method_id = input.paymentMethodId,
            merchant = input.merchant?.takeIf { it.isNotBlank() },
            notes = input.notes?.takeIf { it.isNotBlank() },
            expense_date = input.date.toString(),
            receipt_image_path = input.receiptImagePath,
            subtotal_minor_units = input.subtotal?.minorUnits,
            tax_minor_units = input.tax?.minorUnits,
            discount_minor_units = input.discount?.minorUnits,
            receipt_number = input.receiptNumber,
            needs_review = if (input.needsReview) 1L else 0L,
            created_at = now,
            updated_at = now
        )
        val id = expenseQueries.lastInsertRowId().executeAsOne()
        insertLineItems(id, input)
        return id
    }

    suspend fun update(id: Long, input: ExpenseInput) {
        expenseQueries.update(
            amount_minor_units = input.amount.minorUnits,
            currency_code = input.amount.currencyCode,
            category_id = input.categoryId,
            payment_method_id = input.paymentMethodId,
            merchant = input.merchant?.takeIf { it.isNotBlank() },
            notes = input.notes?.takeIf { it.isNotBlank() },
            expense_date = input.date.toString(),
            receipt_image_path = input.receiptImagePath,
            subtotal_minor_units = input.subtotal?.minorUnits,
            tax_minor_units = input.tax?.minorUnits,
            discount_minor_units = input.discount?.minorUnits,
            receipt_number = input.receiptNumber,
            needs_review = if (input.needsReview) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds(),
            id = id
        )
        lineItemQueries.deleteByExpenseId(id)
        insertLineItems(id, input)
    }

    suspend fun delete(id: Long) {
        expenseQueries.deleteById(id)
    }

    private fun insertLineItems(expenseId: Long, input: ExpenseInput) {
        input.lineItems.forEach { item ->
            lineItemQueries.insert(
                expense_id = expenseId,
                name = item.name,
                quantity = item.quantity,
                unit_price_minor_units = item.unitPrice?.minorUnits,
                total_minor_units = item.total?.minorUnits
            )
        }
    }

    suspend fun getTotalBetween(dateFrom: LocalDate, dateTo: LocalDate, currencyCode: String): Money {
        val total = expenseQueries.selectTotalBetween(dateFrom.toString(), dateTo.toString()).executeAsOne()
        return Money(total, currencyCode)
    }

    suspend fun getCountBetween(dateFrom: LocalDate, dateTo: LocalDate): Long =
        expenseQueries.selectCountBetween(dateFrom.toString(), dateTo.toString()).executeAsOne()

    suspend fun getLargestBetween(dateFrom: LocalDate, dateTo: LocalDate): Expense? =
        expenseQueries.selectLargestBetween(dateFrom.toString(), dateTo.toString())
            .executeAsOneOrNull()
            ?.toDomain(lineItems = emptyList())

    /** Category id to total spent, highest first - the ordering the SQL already does. */
    suspend fun getCategoryTotalsBetween(dateFrom: LocalDate, dateTo: LocalDate): List<Pair<Long, Long>> =
        expenseQueries.selectCategoryTotalsBetween(dateFrom.toString(), dateTo.toString())
            .executeAsList()
            .map { it.category_id to (it.total ?: 0L) }

    /** Calendar date to total spent that day. */
    suspend fun getDailyTotalsBetween(dateFrom: LocalDate, dateTo: LocalDate): Map<LocalDate, Long> =
        expenseQueries.selectDailyTotalsBetween(dateFrom.toString(), dateTo.toString())
            .executeAsList()
            .associate { LocalDate.parse(it.expense_date) to (it.total ?: 0L) }
}
