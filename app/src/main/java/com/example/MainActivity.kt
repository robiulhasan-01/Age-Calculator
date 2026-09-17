package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoDaysBorder
import com.example.ui.theme.BentoDaysContainer
import com.example.ui.theme.BentoDaysText
import com.example.ui.theme.BentoMonthsBorder
import com.example.ui.theme.BentoMonthsContainer
import com.example.ui.theme.BentoMonthsText
import com.example.ui.theme.BentoNextBirthdayBg
import com.example.ui.theme.BentoNextBirthdayBorder
import com.example.ui.theme.BentoOnSurface
import com.example.ui.theme.BentoOnSurfaceVariant
import com.example.ui.theme.BentoOutline
import com.example.ui.theme.BentoOutlineVariant
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoYearsBorder
import com.example.ui.theme.BentoYearsContainer
import com.example.ui.theme.BentoYearsText
import com.example.ui.theme.MyApplicationTheme
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AgeCalculatorApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeCalculatorApp() {
  var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
  var showDatePicker by remember { mutableStateOf(false) }

  val today = remember { LocalDate.now() }
  val ageResult by remember(selectedDate) {
    derivedStateOf {
      selectedDate?.let { date ->
        AgeCalculator.calculateAge(
          birthYear = date.year,
          birthMonth = date.monthValue,
          birthDay = date.dayOfMonth,
          today = today
        )
      }
    }
  }

  val context = LocalContext.current

  fun shareAgeResults(result: AgeResult) {
    val shareText = AgeCalculator.generateShareText(result)
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, shareText)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Age Results")
    context.startActivity(shareIntent)
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = BentoBackground
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp)
      ) {
        // Bento Header
        BentoHeader(
          onReset = { selectedDate = null },
          onShare = {
            ageResult?.let { shareAgeResults(it) }
          },
          hasSelection = selectedDate != null
        )

        // Scrollable Bento Content
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Date of Birth Bento Tile (Clickable Input Card)
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("date_selection_card")
              .clickable { showDatePicker = true },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            border = BorderStroke(1.dp, BentoOutline),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "DATE OF BIRTH",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = BentoPrimary
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                if (selectedDate != null) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = AgeCalculator.formatDate(selectedDate!!),
                      fontSize = 24.sp,
                      fontWeight = FontWeight.Normal,
                      color = BentoOnSurface
                    )
                    Text(
                      text = "Born on ${selectedDate!!.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}",
                      fontSize = 13.sp,
                      color = BentoOnSurfaceVariant
                    )
                  }
                } else {
                  Text(
                    text = stringResource(R.string.select_date_prompt),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    color = BentoOnSurfaceVariant,
                    modifier = Modifier.weight(1f)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(18.dp),
                  color = BentoPrimary,
                  modifier = Modifier.size(50.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.DateRange,
                      contentDescription = stringResource(R.string.select_birth_date),
                      tint = Color.White,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
              }
            }
          }

          // Bento Grid of Age Metrics
          AnimatedVisibility(
            visible = ageResult != null,
            enter = fadeIn() + slideInVertically { it / 4 }
          ) {
            ageResult?.let { result ->
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
              ) {
                // Card 1: Bento Span-2 Total Age / Years
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_age_result"),
                  shape = RoundedCornerShape(32.dp),
                  colors = CardDefaults.cardColors(containerColor = BentoYearsContainer),
                  border = BorderStroke(1.dp, BentoYearsBorder)
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(24.dp)
                      .testTag("years_stat")
                  ) {
                    Text(
                      text = "TOTAL AGE",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.5.sp,
                      color = BentoYearsText.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                      verticalAlignment = Alignment.Bottom,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Text(
                        text = result.years.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoYearsText,
                        letterSpacing = (-2).sp,
                        lineHeight = 76.sp
                      )
                      Text(
                        text = stringResource(R.string.years_label),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = BentoYearsText.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 12.dp)
                      )
                    }
                  }
                }

                // Row with 2 Bento Tiles: Months & Days
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  // Bento Tile: Months
                  Card(
                    modifier = Modifier
                      .weight(1f)
                      .testTag("months_stat"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoMonthsContainer),
                    border = BorderStroke(1.dp, BentoMonthsBorder)
                  ) {
                    Column(
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(20.dp),
                      verticalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "MONTHS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = BentoMonthsText.copy(alpha = 0.8f)
                      )
                      Text(
                        text = result.months.toString(),
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoMonthsText,
                        letterSpacing = (-1).sp
                      )
                    }
                  }

                  // Bento Tile: Days
                  Card(
                    modifier = Modifier
                      .weight(1f)
                      .testTag("days_stat"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoDaysContainer),
                    border = BorderStroke(1.dp, BentoDaysBorder)
                  ) {
                    Column(
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(20.dp),
                      verticalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "DAYS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = BentoDaysText.copy(alpha = 0.8f)
                      )
                      Text(
                        text = result.days.toString(),
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoDaysText,
                        letterSpacing = (-1).sp
                      )
                    }
                  }
                }

                // Bento Tile: Next Birthday with prominent days remaining countdown
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(28.dp),
                  colors = CardDefaults.cardColors(containerColor = BentoNextBirthdayBg),
                  border = BorderStroke(1.dp, BentoNextBirthdayBorder)
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Surface(
                          shape = CircleShape,
                          color = BentoPrimary.copy(alpha = 0.12f),
                          modifier = Modifier.size(32.dp)
                        ) {
                          Box(contentAlignment = Alignment.Center) {
                            Icon(
                              imageVector = Icons.Default.Cake,
                              contentDescription = null,
                              tint = BentoPrimary,
                              modifier = Modifier.size(18.dp)
                            )
                          }
                        }

                        Text(
                          text = "NEXT BIRTHDAY",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          letterSpacing = 1.5.sp,
                          color = Color(0xFF44474E)
                        )
                      }

                      // Prominent pill badge showing days remaining
                      Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (result.daysToNextBirthday == 0L) Color(0xFF2E7D32) else BentoPrimary,
                        modifier = Modifier.testTag("days_remaining_badge")
                      ) {
                        Text(
                          text = if (result.daysToNextBirthday == 0L) {
                            "TODAY!"
                          } else {
                            "${result.daysToNextBirthday} DAYS LEFT"
                          },
                          color = Color.White,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          letterSpacing = 0.8.sp,
                          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                      }
                    }

                    // Main Countdown Display
                    if (result.daysToNextBirthday == 0L) {
                      Text(
                        text = "Happy Birthday today! 🎉🎂",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoOnSurface
                      )
                    } else {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Text(
                          text = result.daysToNextBirthday.toString(),
                          fontSize = 44.sp,
                          fontWeight = FontWeight.Bold,
                          letterSpacing = (-1).sp,
                          color = BentoPrimary,
                          lineHeight = 46.sp
                        )
                        Column(modifier = Modifier.padding(bottom = 6.dp)) {
                          Text(
                            text = "days remaining",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoOnSurface
                          )
                          val detailedBreakdown = buildString {
                            if (result.monthsToNextBirthday > 0) {
                              append("${result.monthsToNextBirthday} month${if (result.monthsToNextBirthday > 1) "s" else ""}")
                            }
                            if (result.monthsToNextBirthday > 0 && result.remainingDaysToNextBirthday > 0) {
                              append(", ")
                            }
                            if (result.remainingDaysToNextBirthday > 0 || result.monthsToNextBirthday == 0) {
                              append("${result.remainingDaysToNextBirthday} day${if (result.remainingDaysToNextBirthday > 1) "s" else ""}")
                            }
                            append(" to go")
                          }
                          Text(
                            text = detailedBreakdown,
                            fontSize = 13.sp,
                            color = Color(0xFF44474E)
                          )
                        }
                      }
                    }
                  }
                }

                // Bento Tile: Life Statistics (2x2 Grid)
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(28.dp),
                  colors = CardDefaults.cardColors(containerColor = BentoSurface),
                  border = BorderStroke(1.dp, BentoOutline)
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                  ) {
                    Text(
                      text = "LIFE STATISTICS",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.5.sp,
                      color = BentoPrimary
                    )

                    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                      MiniStatTile(
                        label = "TOTAL DAYS",
                        value = formatter.format(result.totalDays),
                        modifier = Modifier.weight(1f)
                      )
                      MiniStatTile(
                        label = "TOTAL WEEKS",
                        value = formatter.format(result.totalWeeks),
                        modifier = Modifier.weight(1f)
                      )
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                      MiniStatTile(
                        label = "TOTAL HOURS",
                        value = formatter.format(result.totalDays * 24),
                        modifier = Modifier.weight(1f)
                      )
                      MiniStatTile(
                        label = "BORN DAY",
                        value = result.dayOfWeekBorn,
                        modifier = Modifier.weight(1f)
                      )
                    }
                  }
                }
              }
            }
          }

          // Empty state placeholder if no date is picked
          if (selectedDate == null) {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              shape = RoundedCornerShape(28.dp),
              colors = CardDefaults.cardColors(containerColor = BentoSurfaceVariant),
              border = BorderStroke(1.dp, BentoOutlineVariant)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 24.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Surface(
                  shape = CircleShape,
                  color = BentoPrimary.copy(alpha = 0.12f),
                  modifier = Modifier.size(64.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.CalendarMonth,
                      contentDescription = null,
                      tint = BentoPrimary,
                      modifier = Modifier.size(32.dp)
                    )
                  }
                }

                Text(
                  text = "No Birth Date Selected",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BentoOnSurface
                )

                Text(
                  text = "Tap the card above or the button below to view your age broken down into a Bento Grid.",
                  fontSize = 14.sp,
                  textAlign = TextAlign.Center,
                  color = BentoOnSurfaceVariant,
                  lineHeight = 20.sp
                )
              }
            }
          }
        }

        // Bento Footer with Action Buttons
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (ageResult != null) {
            Button(
              onClick = { shareAgeResults(ageResult!!) },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("share_results_button")
                .shadow(4.dp, shape = RoundedCornerShape(28.dp), ambientColor = BentoPrimary.copy(alpha = 0.25f)),
              shape = RoundedCornerShape(28.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = BentoPrimary,
                contentColor = Color.White
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Share,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = "Share Age Results",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            OutlinedButton(
              onClick = { showDatePicker = true },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("select_date_button"),
              shape = RoundedCornerShape(28.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = BentoPrimary
              ),
              border = BorderStroke(1.5.dp, BentoPrimary)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DateRange,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = "Change Birth Date",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          } else {
            Button(
              onClick = { showDatePicker = true },
              modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("select_date_button")
                .shadow(8.dp, shape = RoundedCornerShape(32.dp), ambientColor = BentoPrimary.copy(alpha = 0.3f)),
              shape = RoundedCornerShape(32.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = BentoPrimary,
                contentColor = Color.White
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DateRange,
                  contentDescription = null,
                  modifier = Modifier.size(22.dp)
                )
                Text(
                  text = stringResource(R.string.select_birth_date),
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }
    }

    // Material 3 Date Picker Dialog
    if (showDatePicker) {
      Material3DatePickerModal(
        onDateSelected = { date ->
          selectedDate = date
          showDatePicker = false
        },
        onDismiss = { showDatePicker = false }
      )
    }
  }
}

@Composable
private fun BentoHeader(
  onReset: () -> Unit,
  onShare: () -> Unit,
  hasSelection: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Surface(
        shape = CircleShape,
        color = BentoSurfaceVariant,
        modifier = Modifier.size(44.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = BentoPrimary,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Text(
        text = stringResource(R.string.app_name),
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        color = BentoOnSurface,
        letterSpacing = (-0.5).sp
      )
    }

    if (hasSelection) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Header Share Button
        Surface(
          shape = CircleShape,
          color = BentoSurfaceVariant,
          modifier = Modifier
            .size(40.dp)
            .testTag("share_button")
            .clickable { onShare() }
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = stringResource(R.string.share_age_button),
              tint = BentoPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // Header Clear Button
        Surface(
          shape = CircleShape,
          color = BentoSurfaceVariant,
          modifier = Modifier
            .size(40.dp)
            .testTag("clear_button")
            .clickable { onReset() }
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = stringResource(R.string.clear),
              tint = BentoOnSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MiniStatTile(
  label: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = BentoSurfaceVariant),
    border = BorderStroke(1.dp, BentoOutlineVariant)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = Color(0xFF44474E)
      )
      Text(
        text = value,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = BentoOnSurface
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3DatePickerModal(
  onDateSelected: (LocalDate) -> Unit,
  onDismiss: () -> Unit
) {
  val datePickerState = rememberDatePickerState(
    selectableDates = object : SelectableDates {
      override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val todayUtcMillis = LocalDate.now()
          .atStartOfDay(ZoneOffset.UTC)
          .toInstant()
          .toEpochMilli()
        return utcTimeMillis <= todayUtcMillis
      }
    }
  )

  DatePickerDialog(
    modifier = Modifier.testTag("date_picker_dialog"),
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = {
          datePickerState.selectedDateMillis?.let { millis ->
            val localDate = Instant.ofEpochMilli(millis)
              .atZone(ZoneOffset.UTC)
              .toLocalDate()
            onDateSelected(localDate)
          }
        },
        enabled = datePickerState.selectedDateMillis != null
      ) {
        Text("OK", color = BentoPrimary, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = BentoOnSurfaceVariant)
      }
    }
  ) {
    DatePicker(state = datePickerState)
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun AgeCalculatorPreview() {
  MyApplicationTheme {
    AgeCalculatorApp()
  }
}


