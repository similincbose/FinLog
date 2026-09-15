package me.riafy.finlog.data.models

/**
 * An exact amount of money: an integer count of minor units (paise, cents, ...)
 * plus the currency it's denominated in. Never a Double - rounding error in a
 * running expense total is not acceptable.
 */
data class Money(val minorUnits: Long, val currencyCode: String) : Comparable<Money> {

    operator fun plus(other: Money): Money {
        require(currencyCode == other.currencyCode) { "Cannot add $currencyCode to ${other.currencyCode}" }
        return Money(minorUnits + other.minorUnits, currencyCode)
    }

    operator fun minus(other: Money): Money {
        require(currencyCode == other.currencyCode) { "Cannot subtract ${other.currencyCode} from $currencyCode" }
        return Money(minorUnits - other.minorUnits, currencyCode)
    }

    override fun compareTo(other: Money): Int {
        require(currencyCode == other.currencyCode) { "Cannot compare $currencyCode to ${other.currencyCode}" }
        return minorUnits.compareTo(other.minorUnits)
    }

    val isZero: Boolean get() = minorUnits == 0L
    val isNegative: Boolean get() = minorUnits < 0L

    companion object {
        fun zero(currencyCode: String) = Money(0L, currencyCode)

        /**
         * Parses a user-typed decimal string (e.g. "1,248.50" or "1248") into minor
         * units for [currencyCode]. Returns null for anything that isn't a plain
         * non-negative decimal, so the caller can show a validation error instead
         * of guessing what the user meant.
         */
        fun fromInput(text: String, currencyCode: String): Money? {
            val cleaned = text.replace(",", "").trim()
            if (cleaned.isEmpty()) return null
            val decimalDigits = CurrencyInfo.decimalDigits(currencyCode)

            val parts = cleaned.split(".")
            if (parts.size > 2) return null

            val wholePart = parts[0].ifEmpty { "0" }
            if (!wholePart.all { it.isDigit() }) return null

            val fractionPart = parts.getOrNull(1) ?: ""
            if (!fractionPart.all { it.isDigit() } || fractionPart.length > decimalDigits) return null

            val paddedFraction = fractionPart.padEnd(decimalDigits, '0')
            val scale = pow10(decimalDigits)
            val whole = wholePart.toLongOrNull() ?: return null
            val fraction = if (decimalDigits == 0) 0L else (paddedFraction.toLongOrNull() ?: return null)

            return Money(whole * scale + fraction, currencyCode)
        }

        private fun pow10(exponent: Int): Long {
            var result = 1L
            repeat(exponent) { result *= 10 }
            return result
        }
    }
}

/** Minor-unit exponents for the currencies this app can be set to. Defaults to 2 for anything unlisted. */
object CurrencyInfo {

    data class Entry(val code: String, val symbol: String, val displayName: String, val decimalDigits: Int)

    val supported = listOf(
        Entry("INR", "₹", "Indian Rupee", 2),
        Entry("USD", "$", "US Dollar", 2),
        Entry("EUR", "€", "Euro", 2),
        Entry("GBP", "£", "British Pound", 2),
        Entry("JPY", "¥", "Japanese Yen", 0),
        Entry("AED", "د.إ", "UAE Dirham", 2)
    )

    fun symbolFor(currencyCode: String): String =
        supported.find { it.code == currencyCode }?.symbol ?: currencyCode

    fun decimalDigits(currencyCode: String): Int =
        supported.find { it.code == currencyCode }?.decimalDigits ?: 2
}
