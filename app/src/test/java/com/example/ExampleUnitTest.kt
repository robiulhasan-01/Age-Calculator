package com.example

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testAgeCalculation_preciseYearsMonthsDays() {
    val birthDate = LocalDate.of(2000, 1, 15)
    val testToday = LocalDate.of(2025, 4, 20)

    val result = AgeCalculator.calculateAge(
      birthYear = birthDate.year,
      birthMonth = birthDate.monthValue,
      birthDay = birthDate.dayOfMonth,
      today = testToday
    )

    assertNotNull(result)
    assertEquals(25, result!!.years)
    assertEquals(3, result.months)
    assertEquals(5, result.days)
  }

  @Test
  fun testAgeCalculation_futureDateReturnsNull() {
    val testToday = LocalDate.of(2025, 1, 1)
    val result = AgeCalculator.calculateAge(
      birthYear = 2026,
      birthMonth = 1,
      birthDay = 1,
      today = testToday
    )

    assertNull(result)
  }

  @Test
  fun testAgeCalculation_bornToday() {
    val testToday = LocalDate.of(2025, 6, 10)
    val result = AgeCalculator.calculateAge(
      birthYear = 2025,
      birthMonth = 6,
      birthDay = 10,
      today = testToday
    )

    assertNotNull(result)
    assertEquals(0, result!!.years)
    assertEquals(0, result.months)
    assertEquals(0, result.days)
    assertEquals(0L, result.totalDays)
  }

  @Test
  fun testAgeCalculation_birthdayToday() {
    val testToday = LocalDate.of(2025, 5, 12)
    val result = AgeCalculator.calculateAge(
      birthYear = 1995,
      birthMonth = 5,
      birthDay = 12,
      today = testToday
    )

    assertNotNull(result)
    assertEquals(30, result!!.years)
    assertEquals(0, result.months)
    assertEquals(0, result.days)
    assertEquals(0L, result.daysToNextBirthday)
  }

  @Test
  fun testNextBirthday_daysRemainingCalculation() {
    val birthDate = LocalDate.of(1998, 10, 25)
    val testToday = LocalDate.of(2025, 10, 20)
    val result = AgeCalculator.calculateAge(
      birthYear = birthDate.year,
      birthMonth = birthDate.monthValue,
      birthDay = birthDate.dayOfMonth,
      today = testToday
    )

    assertNotNull(result)
    assertEquals(5L, result!!.daysToNextBirthday)
    assertEquals(0, result.monthsToNextBirthday)
    assertEquals(5, result.remainingDaysToNextBirthday)
  }

  @Test
  fun testNextBirthday_daysRemainingAcrossYears() {
    val birthDate = LocalDate.of(2000, 2, 10)
    val testToday = LocalDate.of(2024, 12, 1)
    val result = AgeCalculator.calculateAge(
      birthYear = birthDate.year,
      birthMonth = birthDate.monthValue,
      birthDay = birthDate.dayOfMonth,
      today = testToday
    )

    assertNotNull(result)
    // Between 2024-12-01 and 2025-02-10 is 31 days (Dec) + 31 days (Jan) + 9 days = 71 days
    assertEquals(71L, result!!.daysToNextBirthday)
  }

  @Test
  fun testGenerateShareText_containsExpectedContent() {
    val birthDate = LocalDate.of(2000, 1, 15)
    val testToday = LocalDate.of(2025, 4, 20)
    val result = AgeCalculator.calculateAge(
      birthYear = birthDate.year,
      birthMonth = birthDate.monthValue,
      birthDay = birthDate.dayOfMonth,
      today = testToday
    )

    assertNotNull(result)
    val shareText = AgeCalculator.generateShareText(result!!)
    org.junit.Assert.assertTrue(shareText.contains("25 Years, 3 Months, 5 Days"))
    org.junit.Assert.assertTrue(shareText.contains("Next Birthday"))
    org.junit.Assert.assertTrue(shareText.contains("days remaining"))
  }
}

