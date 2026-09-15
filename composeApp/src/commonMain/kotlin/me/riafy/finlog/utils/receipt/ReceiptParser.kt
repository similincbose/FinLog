package me.riafy.finlog.utils.receipt

import me.riafy.finlog.data.models.DefaultPaymentMethods
import me.riafy.finlog.data.models.Money
import me.riafy.finlog.data.models.ParsedLineItem
import me.riafy.finlog.data.models.ParsedReceipt
import me.riafy.finlog.data.models.ReceiptField
import me.riafy.finlog.data.models.RecognizedText

/**
 * Turns OCR output into a structured, editable guess at what the receipt says.
 * Deliberately defensive: receipts vary wildly in layout, so every rule here is
 * a heuristic over plain text rather than a fixed template, and a field that
 * can't be found honestly comes back null instead of a fabricated value.
 */
object ReceiptParser {

    private val totalLabels = listOf("grand total", "total amount", "net amount", "amount due", "total")
    private val subtotalLabels = listOf("subtotal", "sub total", "sub-total")
    private val taxLabels = listOf("cgst", "sgst", "gst", "vat", "tax")
    private val discountLabels = listOf("discount", "you saved", "less disc")
    private val receiptNumberLabels = listOf("receipt no", "invoice no", "bill no", "order no", "ref no")

    private val lineItemPattern = Regex("""^(.{2,40}?)\s+([\d][\d,]*\.?\d{0,2})$""")
    private val leadingQuantityPattern = Regex("""^(\d+(?:\.\d+)?)\s*[xX×]\s*(.+)$""")

    fun parse(recognized: RecognizedText, currencyCode: String): ParsedReceipt {
        val lines = recognized.lines.map { it.trim() }.filter { it.isNotEmpty() }
        val lowerLines = lines.map { it.lowercase() }

        val merchant = findMerchant(lines)
        val date = ReceiptDateExtractor.findFirst(lines)

        val total = findLabeledAmount(lines, lowerLines, totalLabels, currencyCode, excludeLabels = subtotalLabels)
        val subtotal = findLabeledAmount(lines, lowerLines, subtotalLabels, currencyCode)
        val tax = findLabeledAmount(lines, lowerLines, taxLabels, currencyCode)
        val discount = findLabeledAmount(lines, lowerLines, discountLabels, currencyCode)
        val receiptNumber = findLabeledText(lines, lowerLines, receiptNumberLabels)
        val paymentMethodGuess = findPaymentMethod(lowerLines)

        val claimedLineIndexes = linesClaimedByLabels(
            lowerLines,
            totalLabels + subtotalLabels + taxLabels + discountLabels + receiptNumberLabels
        )
        val lineItems = findLineItems(lines, claimedLineIndexes, currencyCode)

        val uncertain = mutableSetOf<ReceiptField>()
        if (merchant == null) uncertain += ReceiptField.MERCHANT
        if (date == null) uncertain += ReceiptField.DATE
        if (total == null) uncertain += ReceiptField.TOTAL

        val reconciles = reconciles(total, subtotal, tax, discount)
        if (reconciles == false) {
            uncertain += ReceiptField.TOTAL
            uncertain += ReceiptField.SUBTOTAL
        }

        return ParsedReceipt(
            merchant = merchant,
            date = date,
            total = total,
            subtotal = subtotal,
            tax = tax,
            discount = discount,
            receiptNumber = receiptNumber,
            paymentMethodGuess = paymentMethodGuess,
            lineItems = lineItems,
            uncertainFields = uncertain,
            hasValidationWarning = total == null || reconciles == false
        )
    }

    /**
     * The first line that reads like a name rather than a date, a lone number,
     * or a short code - receipts almost always lead with the merchant name.
     */
    private fun findMerchant(lines: List<String>): String? = lines.firstOrNull { line ->
        val letters = line.count { it.isLetter() }
        letters >= 3 && ReceiptDateExtractor.findFirst(listOf(line)) == null
    }

    private fun findLabeledAmount(
        lines: List<String>,
        lowerLines: List<String>,
        labels: List<String>,
        currencyCode: String,
        excludeLabels: List<String> = emptyList()
    ): Money? {
        for (label in labels) {
            val index = lowerLines.indexOfFirst { it.contains(label) && excludeLabels.none(it::contains) }
            if (index == -1) continue
            MoneyTextExtractor.lastAmountOn(lines[index], currencyCode)?.let { return it }
        }
        return null
    }

    private fun findLabeledText(lines: List<String>, lowerLines: List<String>, labels: List<String>): String? {
        for (label in labels) {
            val index = lowerLines.indexOfFirst { it.contains(label) }
            if (index == -1) continue
            val afterLabel = lines[index].substringAfter(':', lines[index])
                .replace(Regex("(?i)$label"), "")
                .trim(' ', ':', '-')
            if (afterLabel.isNotBlank()) return afterLabel
        }
        return null
    }

    private fun findPaymentMethod(lowerLines: List<String>): String? {
        val joined = lowerLines.joinToString(" ")
        return when {
            "upi" in joined -> "UPI"
            "credit card" in joined -> "Credit Card"
            "debit card" in joined -> "Debit Card"
            "card" in joined -> "Credit Card"
            "cash" in joined -> "Cash"
            else -> null
        }.takeIf { it in DefaultPaymentMethods.names }
    }

    private fun linesClaimedByLabels(lowerLines: List<String>, labels: List<String>): Set<Int> =
        lowerLines.indices.filter { index -> labels.any { lowerLines[index].contains(it) } }.toSet()

    private fun findLineItems(lines: List<String>, claimedIndexes: Set<Int>, currencyCode: String): List<ParsedLineItem> {
        val items = mutableListOf<ParsedLineItem>()
        lines.forEachIndexed { index, line ->
            if (index in claimedIndexes) return@forEachIndexed
            val match = lineItemPattern.find(line) ?: return@forEachIndexed
            val (rawName, rawAmount) = match.destructured
            val total = Money.fromInput(rawAmount, currencyCode) ?: return@forEachIndexed

            val quantityMatch = leadingQuantityPattern.find(rawName.trim())
            val quantity = quantityMatch?.groupValues?.get(1)?.toDoubleOrNull()
            val name = (quantityMatch?.groupValues?.get(2) ?: rawName).trim()
            if (name.length < 2) return@forEachIndexed

            items += ParsedLineItem(name = name, quantity = quantity, unitPrice = null, total = total)
        }
        return items
    }

    /** Null when there isn't enough data to check; true/false once there is. */
    private fun reconciles(total: Money?, subtotal: Money?, tax: Money?, discount: Money?): Boolean? {
        if (total == null || subtotal == null) return null
        val expected = subtotal + (tax ?: Money.zero(subtotal.currencyCode)) - (discount ?: Money.zero(subtotal.currencyCode))
        val tolerance = maxOf(100L, total.minorUnits / 100L)
        return kotlin.math.abs(expected.minorUnits - total.minorUnits) <= tolerance
    }
}
