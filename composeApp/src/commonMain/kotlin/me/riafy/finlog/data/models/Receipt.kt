package me.riafy.finlog.data.models

import kotlinx.datetime.LocalDate

/** What OCR answers: "what text is visible?" - nothing here knows what any of it means yet. */
data class RecognizedText(val fullText: String, val lines: List<String>)

/** A field the parser could only guess at, or couldn't find - the review screen highlights these. */
enum class ReceiptField {
    MERCHANT, DATE, TOTAL, SUBTOTAL, TAX, DISCOUNT
}

/**
 * What the parser answers: "what does this text mean?" Every field is nullable
 * on purpose - a receipt missing tax should produce tax = null, never a guessed
 * zero.
 */
data class ParsedReceipt(
    val merchant: String?,
    val date: LocalDate?,
    val total: Money?,
    val subtotal: Money?,
    val tax: Money?,
    val discount: Money?,
    val receiptNumber: String?,
    val paymentMethodGuess: String?,
    val lineItems: List<ParsedLineItem>,
    val uncertainFields: Set<ReceiptField>,
    /** Subtotal + tax - discount didn't reconcile with the total, or no total was found at all. */
    val hasValidationWarning: Boolean
)

data class ParsedLineItem(
    val name: String,
    val quantity: Double?,
    val unitPrice: Money?,
    val total: Money?
)
