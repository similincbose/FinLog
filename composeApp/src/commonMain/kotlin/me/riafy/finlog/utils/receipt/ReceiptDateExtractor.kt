package me.riafy.finlog.utils.receipt

import kotlinx.datetime.LocalDate

/**
 * Receipts don't agree on a date format, so this tries the common shapes in
 * order and keeps the first one that produces a real calendar date. Never
 * guesses at a date it can't actually parse - the caller treats a null result
 * as "ask the user".
 */
object ReceiptDateExtractor {

    private val numericPattern = Regex("""\b(\d{1,2})[/.\-](\d{1,2})[/.\-](\d{2,4})\b""")
    private val isoPattern = Regex("""\b(\d{4})-(\d{1,2})-(\d{1,2})\b""")
    private val monthNamePattern = Regex(
        """\b(\d{1,2})\s+([A-Za-z]{3,9})[,]?\s+(\d{2,4})\b|\b([A-Za-z]{3,9})\s+(\d{1,2})[,]?\s+(\d{2,4})\b"""
    )

    private val monthByName = mapOf(
        "jan" to 1, "january" to 1,
        "feb" to 2, "february" to 2,
        "mar" to 3, "march" to 3,
        "apr" to 4, "april" to 4,
        "may" to 5,
        "jun" to 6, "june" to 6,
        "jul" to 7, "july" to 7,
        "aug" to 8, "august" to 8,
        "sep" to 9, "sept" to 9, "september" to 9,
        "oct" to 10, "october" to 10,
        "nov" to 11, "november" to 11,
        "dec" to 12, "december" to 12
    )

    fun findFirst(lines: List<String>): LocalDate? {
        for (line in lines) {
            findIn(line)?.let { return it }
        }
        return null
    }

    private fun findIn(line: String): LocalDate? {
        isoPattern.find(line)?.let { match ->
            val (year, month, day) = match.destructured
            toLocalDateOrNull(year.toInt(), month.toInt(), day.toInt())?.let { return it }
        }

        monthNamePattern.find(line)?.let { match ->
            val groups = match.groupValues
            val (day, monthName, year) = if (groups[1].isNotEmpty()) {
                Triple(groups[1], groups[2], groups[3])
            } else {
                Triple(groups[5], groups[4], groups[6])
            }
            val month = monthByName[monthName.lowercase()] ?: return@let
            toLocalDateOrNull(fullYear(year.toInt()), month, day.toInt())?.let { return it }
        }

        numericPattern.find(line)?.let { match ->
            val (first, second, yearRaw) = match.destructured
            val year = fullYear(yearRaw.toInt())
            val a = first.toInt()
            val b = second.toInt()
            // Receipts here are overwhelmingly day-first; only fall back to
            // month-first when the first number can't be a day.
            val (day, month) = if (a in 1..31 && b in 1..12) a to b else b to a
            toLocalDateOrNull(year, month, day)?.let { return it }
        }

        return null
    }

    private fun fullYear(year: Int): Int = if (year < 100) 2000 + year else year

    private fun toLocalDateOrNull(year: Int, month: Int, day: Int): LocalDate? {
        if (month !in 1..12) return null
        return try {
            LocalDate(year, month, day)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
