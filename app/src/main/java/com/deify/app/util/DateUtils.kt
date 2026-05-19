package com.deify.app.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayFormatter = DateTimeFormatter.ofPattern("MM月dd日")

    fun today(): LocalDate = LocalDate.now()

    fun format(date: LocalDate): String = date.format(dateFormatter)

    fun displayFormat(date: LocalDate): String = date.format(displayFormatter)

    fun parse(dateStr: String): LocalDate = LocalDate.parse(dateStr, dateFormatter)

    fun weekOfYear(date: LocalDate): Int = date.get(WeekFields.of(Locale.CHINA).weekOfWeekBasedYear())

    fun daysInMonth(date: LocalDate): Int = date.lengthOfMonth()
}
