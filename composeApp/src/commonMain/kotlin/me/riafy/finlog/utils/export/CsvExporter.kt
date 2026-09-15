package me.riafy.finlog.utils.export

import me.riafy.finlog.data.models.Expense
import me.riafy.finlog.utils.money.MoneyFormatter

/**
 * Builds the CSV file backing Settings > Export. A pure, platform-independent
 * function so the row/escaping logic can be tested without a database or a
 * share sheet.
 */
object CsvExporter {

    private val header = listOf("Date", "Merchant", "Category", "Amount", "Currency", "Payment Method", "Notes")

    fun build(expenses: List<Expense>): String {
        val rows = expenses.map { expense ->
            listOf(
                expense.date.toString(),
                expense.merchant.orEmpty(),
                expense.category.name,
                MoneyFormatter.formatForInput(expense.amount),
                expense.amount.currencyCode,
                expense.paymentMethod.name,
                expense.notes.orEmpty()
            )
        }
        return (listOf(header) + rows).joinToString("\r\n") { row -> row.joinToString(",") { escape(it) } }
    }

    private fun escape(field: String): String =
        if (field.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
}
