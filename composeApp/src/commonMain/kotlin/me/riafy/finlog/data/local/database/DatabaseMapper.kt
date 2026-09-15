package me.riafy.finlog.data.local.database

import kotlinx.datetime.LocalDate
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.data.models.LineItem
import me.riafy.finlog.data.models.Money
import me.riafy.finlog.data.models.PaymentMethod
import me.riafy.finlog.database.SelectById
import me.riafy.finlog.database.SelectLargestBetween
import me.riafy.finlog.database.SelectRecent
import me.riafy.finlog.database.SelectWithDetails
import kotlin.time.Instant
import me.riafy.finlog.database.Category as CategoryRow
import me.riafy.finlog.database.Line_item as LineItemRow
import me.riafy.finlog.database.Payment_method as PaymentMethodRow

fun CategoryRow.toDomain() = Category(
    id = id,
    name = name,
    iconKey = icon_key,
    colorHex = color_hex,
    sortOrder = sort_order,
    isDefault = is_default != 0L
)

fun PaymentMethodRow.toDomain() = PaymentMethod(
    id = id,
    name = name,
    sortOrder = sort_order,
    isDefault = is_default != 0L
)

fun LineItemRow.toDomain(currencyCode: String) = LineItem(
    id = id,
    name = name,
    quantity = quantity,
    unitPrice = unit_price_minor_units?.let { Money(it, currencyCode) },
    total = total_minor_units?.let { Money(it, currencyCode) }
)

fun SelectWithDetails.toDomain(lineItems: List<LineItem>): Expense = buildExpense(
    id = expense_id,
    amountMinorUnits = expense_amount_minor_units,
    currencyCode = expense_currency_code,
    merchant = expense_merchant,
    notes = expense_notes,
    date = expense_date,
    receiptImagePath = expense_receipt_image_path,
    subtotalMinorUnits = expense_subtotal_minor_units,
    taxMinorUnits = expense_tax_minor_units,
    discountMinorUnits = expense_discount_minor_units,
    receiptNumber = expense_receipt_number,
    needsReview = expense_needs_review,
    createdAt = expense_created_at,
    updatedAt = expense_updated_at,
    categoryId = category_id,
    categoryName = category_name,
    categoryIconKey = category_icon_key,
    categoryColorHex = category_color_hex,
    categorySortOrder = category_sort_order,
    categoryIsDefault = category_is_default,
    paymentMethodId = payment_method_id,
    paymentMethodName = payment_method_name,
    paymentMethodSortOrder = payment_method_sort_order,
    paymentMethodIsDefault = payment_method_is_default,
    lineItems = lineItems
)

fun SelectRecent.toDomain(lineItems: List<LineItem>): Expense = buildExpense(
    id = expense_id,
    amountMinorUnits = expense_amount_minor_units,
    currencyCode = expense_currency_code,
    merchant = expense_merchant,
    notes = expense_notes,
    date = expense_date,
    receiptImagePath = expense_receipt_image_path,
    subtotalMinorUnits = expense_subtotal_minor_units,
    taxMinorUnits = expense_tax_minor_units,
    discountMinorUnits = expense_discount_minor_units,
    receiptNumber = expense_receipt_number,
    needsReview = expense_needs_review,
    createdAt = expense_created_at,
    updatedAt = expense_updated_at,
    categoryId = category_id,
    categoryName = category_name,
    categoryIconKey = category_icon_key,
    categoryColorHex = category_color_hex,
    categorySortOrder = category_sort_order,
    categoryIsDefault = category_is_default,
    paymentMethodId = payment_method_id,
    paymentMethodName = payment_method_name,
    paymentMethodSortOrder = payment_method_sort_order,
    paymentMethodIsDefault = payment_method_is_default,
    lineItems = lineItems
)

fun SelectLargestBetween.toDomain(lineItems: List<LineItem>): Expense = buildExpense(
    id = expense_id,
    amountMinorUnits = expense_amount_minor_units,
    currencyCode = expense_currency_code,
    merchant = expense_merchant,
    notes = expense_notes,
    date = expense_date,
    receiptImagePath = expense_receipt_image_path,
    subtotalMinorUnits = expense_subtotal_minor_units,
    taxMinorUnits = expense_tax_minor_units,
    discountMinorUnits = expense_discount_minor_units,
    receiptNumber = expense_receipt_number,
    needsReview = expense_needs_review,
    createdAt = expense_created_at,
    updatedAt = expense_updated_at,
    categoryId = category_id,
    categoryName = category_name,
    categoryIconKey = category_icon_key,
    categoryColorHex = category_color_hex,
    categorySortOrder = category_sort_order,
    categoryIsDefault = category_is_default,
    paymentMethodId = payment_method_id,
    paymentMethodName = payment_method_name,
    paymentMethodSortOrder = payment_method_sort_order,
    paymentMethodIsDefault = payment_method_is_default,
    lineItems = lineItems
)

fun SelectById.toDomain(lineItems: List<LineItem>): Expense = buildExpense(
    id = expense_id,
    amountMinorUnits = expense_amount_minor_units,
    currencyCode = expense_currency_code,
    merchant = expense_merchant,
    notes = expense_notes,
    date = expense_date,
    receiptImagePath = expense_receipt_image_path,
    subtotalMinorUnits = expense_subtotal_minor_units,
    taxMinorUnits = expense_tax_minor_units,
    discountMinorUnits = expense_discount_minor_units,
    receiptNumber = expense_receipt_number,
    needsReview = expense_needs_review,
    createdAt = expense_created_at,
    updatedAt = expense_updated_at,
    categoryId = category_id,
    categoryName = category_name,
    categoryIconKey = category_icon_key,
    categoryColorHex = category_color_hex,
    categorySortOrder = category_sort_order,
    categoryIsDefault = category_is_default,
    paymentMethodId = payment_method_id,
    paymentMethodName = payment_method_name,
    paymentMethodSortOrder = payment_method_sort_order,
    paymentMethodIsDefault = payment_method_is_default,
    lineItems = lineItems
)

/**
 * selectWithDetails and selectById select the exact same aliased columns, so
 * SQLDelight generates two structurally identical but unrelated row classes.
 * Both toDomain() overloads funnel through here instead of duplicating the
 * field-by-field construction twice.
 */
private fun buildExpense(
    id: Long,
    amountMinorUnits: Long,
    currencyCode: String,
    merchant: String?,
    notes: String?,
    date: String,
    receiptImagePath: String?,
    subtotalMinorUnits: Long?,
    taxMinorUnits: Long?,
    discountMinorUnits: Long?,
    receiptNumber: String?,
    needsReview: Long,
    createdAt: Long,
    updatedAt: Long,
    categoryId: Long,
    categoryName: String,
    categoryIconKey: String,
    categoryColorHex: String,
    categorySortOrder: Long,
    categoryIsDefault: Long,
    paymentMethodId: Long,
    paymentMethodName: String,
    paymentMethodSortOrder: Long,
    paymentMethodIsDefault: Long,
    lineItems: List<LineItem>
) = Expense(
    id = id,
    amount = Money(amountMinorUnits, currencyCode),
    category = Category(
        id = categoryId,
        name = categoryName,
        iconKey = categoryIconKey,
        colorHex = categoryColorHex,
        sortOrder = categorySortOrder,
        isDefault = categoryIsDefault != 0L
    ),
    paymentMethod = PaymentMethod(
        id = paymentMethodId,
        name = paymentMethodName,
        sortOrder = paymentMethodSortOrder,
        isDefault = paymentMethodIsDefault != 0L
    ),
    merchant = merchant,
    notes = notes,
    date = LocalDate.parse(date),
    receiptImagePath = receiptImagePath,
    subtotal = subtotalMinorUnits?.let { Money(it, currencyCode) },
    tax = taxMinorUnits?.let { Money(it, currencyCode) },
    discount = discountMinorUnits?.let { Money(it, currencyCode) },
    receiptNumber = receiptNumber,
    needsReview = needsReview != 0L,
    lineItems = lineItems,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt)
)
