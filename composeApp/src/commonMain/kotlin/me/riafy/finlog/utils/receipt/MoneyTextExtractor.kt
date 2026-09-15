package me.riafy.finlog.utils.receipt

import me.riafy.finlog.data.models.Money

/** Pulls a plausible money amount out of OCR text - "Total: Rs. 1,248.00" -> Money. */
object MoneyTextExtractor {

    private val amountPattern = Regex("""[\d][\d,]*\.?\d{0,2}""")

    /** The last number-looking token on the line, since labels like "Total" come before the amount. */
    fun lastAmountOn(line: String, currencyCode: String): Money? {
        val matches = amountPattern.findAll(line).toList()
        val candidate = matches.lastOrNull { it.value.any(Char::isDigit) } ?: return null
        return Money.fromInput(candidate.value, currencyCode)
    }

    /** Every number-looking token on the line, in order - used for line items that carry qty, price and total. */
    fun allAmountsOn(line: String, currencyCode: String): List<Money> =
        amountPattern.findAll(line)
            .mapNotNull { Money.fromInput(it.value, currencyCode) }
            .toList()
}
