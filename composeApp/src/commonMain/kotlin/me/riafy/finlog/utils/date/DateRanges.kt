package me.riafy.finlog.utils.date

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

data class DateRange(val start: LocalDate, val end: LocalDate)

fun currentMonthRange(today: LocalDate): DateRange =
    DateRange(start = LocalDate(today.year, today.month, 1), end = today)

/** The full previous calendar month, start to last day - not "30 days ago". */
fun previousMonthRange(today: LocalDate): DateRange {
    val currentMonthStart = LocalDate(today.year, today.month, 1)
    val previousMonthEnd = currentMonthStart.minus(1, DateTimeUnit.DAY)
    val previousMonthStart = LocalDate(previousMonthEnd.year, previousMonthEnd.month, 1)
    return DateRange(start = previousMonthStart, end = previousMonthEnd)
}
