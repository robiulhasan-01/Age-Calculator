package com.example

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Result data class containing the calculated age metrics.
 */
data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long,
    val daysToNextBirthday: Long,
    val monthsToNextBirthday: Int,
    val remainingDaysToNextBirthday: Int,
    val dayOfWeekBorn: String,
    val formattedBirthDate: String
)

/**
 * Utility class for calculating age and related statistics.
 */
object AgeCalculator {

    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())

    /**
     * Calculates the age between the given birthDate and today.
     * Returns null if birthDate is after today.
     */
    fun calculateAge(
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int,
        today: LocalDate = LocalDate.now()
    ): AgeResult? {
        val birthDate = try {
            LocalDate.of(birthYear, birthMonth, birthDay)
        } catch (_: Exception) {
            return null
        }

        if (birthDate.isAfter(today)) {
            return null
        }

        val period = Period.between(birthDate, today)
        val years = period.years
        val months = period.months
        val days = period.days

        val totalDays = ChronoUnit.DAYS.between(birthDate, today)
        val totalWeeks = totalDays / 7

        // Next birthday calculation
        var nextBirthday = birthDate.withYear(today.year)
        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1)
        }

        val daysToNextBirthday = ChronoUnit.DAYS.between(today, nextBirthday)
        val nextBirthdayPeriod = Period.between(today, nextBirthday)
        val monthsToNext = nextBirthdayPeriod.months
        val daysToNext = nextBirthdayPeriod.days

        val dayOfWeekBorn = birthDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedDate = birthDate.format(dateFormatter)

        return AgeResult(
            years = years,
            months = months,
            days = days,
            totalDays = totalDays,
            totalWeeks = totalWeeks,
            daysToNextBirthday = daysToNextBirthday,
            monthsToNextBirthday = monthsToNext,
            remainingDaysToNextBirthday = daysToNext,
            dayOfWeekBorn = dayOfWeekBorn,
            formattedBirthDate = formattedDate
        )
    }

    /**
     * Formats a local date into a user-friendly string.
     */
    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    /**
     * Generates a clean, shareable summary of the calculated age and next birthday countdown.
     */
    fun generateShareText(result: AgeResult): String {
        val birthdayCountdown = if (result.daysToNextBirthday == 0L) {
            "Today is my birthday! 🎂🎉"
        } else {
            "${result.daysToNextBirthday} days remaining until next birthday (${result.monthsToNextBirthday} months, ${result.remainingDaysToNextBirthday} days)"
        }

        return buildString {
            appendLine("🎂 My Age Summary")
            appendLine("📅 Date of Birth: ${result.formattedBirthDate}")
            appendLine("✨ Exact Age: ${result.years} Years, ${result.months} Months, ${result.days} Days")
            appendLine("⏳ Next Birthday: $birthdayCountdown")
            appendLine("📊 Life Stats: ${result.totalDays} days lived (${result.totalWeeks} weeks) | Born on a ${result.dayOfWeekBorn}")
            appendLine("Calculated with Age Calculator")
        }
    }
}
