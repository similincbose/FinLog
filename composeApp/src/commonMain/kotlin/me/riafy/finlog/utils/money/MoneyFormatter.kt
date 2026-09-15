package me.riafy.finlog.utils.money

import me.riafy.finlog.data.models.CurrencyInfo
import me.riafy.finlog.data.models.Money

/**
 * Turns a [Money] value into display text. Kept separate from [Money] itself so
 * storage and calculation never depend on how a number happens to be shown.
 */
object MoneyFormatter {

    fun format(money: Money, showSymbol: Boolean = true): String {
        val decimalDigits = CurrencyInfo.decimalDigits(money.currencyCode)
        val negative = money.minorUnits < 0
        val absMinorUnits = if (negative) -money.minorUnits else money.minorUnits

        val scale = pow10(decimalDigits)
        val whole = absMinorUnits / scale
        val fraction = absMinorUnits % scale

        val groupedWhole = if (money.currencyCode == "INR") groupIndian(whole) else groupWestern(whole)
        val amountText = if (decimalDigits == 0) {
            groupedWhole
        } else {
            "$groupedWhole." + fraction.toString().padStart(decimalDigits, '0')
        }

        val symbol = if (showSymbol) CurrencyInfo.symbolFor(money.currencyCode) else ""
        val sign = if (negative) "-" else ""
        return "$sign$symbol$amountText"
    }

    /** Plain "1248.50" with no symbol or grouping - what a text field shows while the user edits it. */
    fun formatForInput(money: Money): String {
        val decimalDigits = CurrencyInfo.decimalDigits(money.currencyCode)
        if (decimalDigits == 0) return money.minorUnits.toString()

        val scale = pow10(decimalDigits)
        val whole = money.minorUnits / scale
        val fraction = money.minorUnits % scale
        return "$whole." + fraction.toString().padStart(decimalDigits, '0')
    }

    /** Whole-rupee amounts for compact spots (summary tiles) - no decimals, still grouped. */
    fun formatWhole(money: Money, showSymbol: Boolean = true): String {
        val decimalDigits = CurrencyInfo.decimalDigits(money.currencyCode)
        val scale = pow10(decimalDigits)
        val rounded = Money(
            minorUnits = (money.minorUnits + (if (money.minorUnits >= 0) scale / 2 else -scale / 2)) / scale * scale,
            currencyCode = money.currencyCode
        )
        return format(rounded, showSymbol)
    }

    private fun groupWestern(whole: Long): String {
        val digits = whole.toString()
        val builder = StringBuilder()
        for (index in digits.indices) {
            if (index > 0 && (digits.length - index) % 3 == 0) builder.append(',')
            builder.append(digits[index])
        }
        return builder.toString()
    }

    /** Indian numbering: last three digits, then pairs - 1234567 -> 12,34,567 */
    private fun groupIndian(whole: Long): String {
        val digits = whole.toString()
        if (digits.length <= 3) return digits

        val lastThree = digits.takeLast(3)
        val remainder = digits.dropLast(3)
        val builder = StringBuilder()
        for (index in remainder.indices) {
            if (index > 0 && (remainder.length - index) % 2 == 0) builder.append(',')
            builder.append(remainder[index])
        }
        return "$builder,$lastThree"
    }

    private fun pow10(exponent: Int): Long {
        var result = 1L
        repeat(exponent) { result *= 10 }
        return result
    }
}
