package me.riafy.finlog.utils.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Material3's DatePicker works in UTC epoch millis regardless of device
 * timezone, so both conversions stay anchored to UTC to round-trip cleanly.
 */
fun LocalDate.toEpochMillisUtc(): Long =
    LocalDateTime(this, LocalTime(0, 0)).toInstant(TimeZone.UTC).toEpochMilliseconds()

fun Long.toLocalDateFromEpochMillisUtc(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
