package me.riafy.finlog.utils.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** "Today" / "Yesterday" / "12 Sep 2026" - presentation only, the stored date is always a plain LocalDate. */
fun LocalDate.asRelativeLabel(today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())): String =
    when (this.daysUntil(today)) {
        0 -> "Today"
        1 -> "Yesterday"
        else -> asDayMonthYear()
    }

fun LocalDate.asDayMonthYear(): String = "$day ${month.shortName()} $year"

fun LocalDate.asMonthYear(): String = "${month.shortName()} $year"

private fun Month.shortName(): String = when (this) {
    Month.JANUARY -> "Jan"
    Month.FEBRUARY -> "Feb"
    Month.MARCH -> "Mar"
    Month.APRIL -> "Apr"
    Month.MAY -> "May"
    Month.JUNE -> "Jun"
    Month.JULY -> "Jul"
    Month.AUGUST -> "Aug"
    Month.SEPTEMBER -> "Sep"
    Month.OCTOBER -> "Oct"
    Month.NOVEMBER -> "Nov"
    Month.DECEMBER -> "Dec"
}
