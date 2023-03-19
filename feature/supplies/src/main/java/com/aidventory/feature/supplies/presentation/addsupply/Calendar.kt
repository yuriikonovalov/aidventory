package com.aidventory.feature.supplies.presentation.addsupply

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.feature.supplies.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun Calendar(
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    initialMonth: YearMonth = YearMonth.now()
) {
    // Keeps a year and a month to be shown.
    var yearMonth by rememberYearMonthState(initialMonth)
    val calendarManager = rememberCalendarManager(
        yearMonth = yearMonth,
        selectedDate = selectedDate,
        minDate = minDate,
        maxDate = maxDate
    )

    // The calendar back button is disabled when then current month is the month of the minDate.
    val isPreviousButtonEnabled = minDate?.let { date ->
        YearMonth.of(date.year, date.month).isBefore(yearMonth)
    } ?: true

    // Show a correct month according to the selected date.
    LaunchedEffect(selectedDate) {
        selectedDate?.let {
            val selectedYearMonth = YearMonth.of(selectedDate.year, selectedDate.month)
            yearMonth = selectedYearMonth
        }
    }

    // List of weeks to be shown.
    val weeks = calendarManager.getCalendarMonth().weeks
    // To keep displaying of the calendar without any distortion.

    /*
    The value is 7.0f because a row of the calendar contains 7 boxes (number of days in a week)
    which have aspect ration 1f (meaning they are squares).
    */
    val calendarWidth = 7.0f
    /*
     Height consists of the number of rows of the calendar that represent weeks
     and 2 additional rows:
          1.Header
          2.Day of week labels
     Header is placed in a column with the other calendar rows in order to keep the header row
     in the same boundaries. It's important for the case when the widthScreenSize is Expanded.
     */
    val calendarHeight = (weeks.size + 2)
    val ratio = calendarWidth / calendarHeight
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .aspectRatio(ratio)
                .fillMaxSize()
        ) {
            Header(
                modifier = Modifier.fillMaxWidth(),
                yearMonth = yearMonth,
                isPreviousButtonEnabled = isPreviousButtonEnabled,
                isNextButtonEnabled = true, // because there's no any restriction on a maximum date.
                onPreviousClick = {
                    yearMonth = yearMonth.minusMonths(1)
                },
                onNextClick = {
                    yearMonth = yearMonth.plusMonths(1)
                }
            )
            DaysOfWeekRow(modifier = Modifier.fillMaxWidth())
            weeks.forEach { days ->
                WeekRow(
                    modifier = Modifier.fillMaxWidth(),
                    days = days,
                    onClick = onDateChanged
                )
            }
        }
    }
}

@Composable
private fun Header(
    yearMonth: YearMonth,
    isPreviousButtonEnabled: Boolean,
    isNextButtonEnabled: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val month = yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
    val year = yearMonth.year
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        /* Weight modifiers in total should be 7 (number of days of week) in order
         to have correct aspect ration.*/
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            OutlinedIconButton(
                enabled = isPreviousButtonEnabled,
                onClick = onPreviousClick
            ) {
                Icon(
                    imageVector = AidventoryIcons.ArrowBack.imageVector,
                    contentDescription = stringResource(R.string.calendar_previous_button)
                )
            }
        }
        Text(
            modifier = Modifier.weight(5f),
            text = "$month $year",
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            OutlinedIconButton(
                enabled = isNextButtonEnabled,
                onClick = onNextClick
            ) {
                Icon(
                    imageVector = AidventoryIcons.ArrowForward.imageVector,
                    contentDescription = stringResource(R.string.calendar_next_button)
                )
            }
        }
    }
}

@Composable
private fun DaysOfWeekRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        DayOfWeek.values().forEach { dayOfWeek ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    maxLines = 1
                )
            }
        }
    }

}

@Composable
private fun WeekRow(
    days: List<DisplayDay?>,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        days.forEach { displayDay ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(1.dp)
                    .clip(dayBoxShape)
                    .isToday(displayDay)
                    .isSelected(displayDay)
                    .clickable(
                        enabled = displayDay?.isEnabled == true,
                        onClick = {
                            displayDay?.let {
                                onClick(it.day.date)
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                displayDay?.let {
                    Text(
                        text = displayDay.day.dayOfMonth.toString(),
                        color = displayDay.color(),
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberYearMonthState(yearMonth: YearMonth = YearMonth.now()) = remember(yearMonth) {
    mutableStateOf(yearMonth)
}

// Shape used to be applied to the box of a day that is either selected or today.
private val dayBoxShape = RoundedCornerShape(8.dp)
private fun Modifier.isSelected(displayDay: DisplayDay?): Modifier = composed {
    if (displayDay?.isSelected == true) {
        this.background(MaterialTheme.colorScheme.tertiary)
    } else {
        this
    }

}

private fun Modifier.isToday(displayDay: DisplayDay?): Modifier {
    return if (displayDay?.isToday == true) {
        this.border(
            border = BorderStroke(1.dp, Color.LightGray),
            shape = dayBoxShape
        )
    } else {
        this
    }
}


@Composable
private fun DisplayDay.color() = when {
    isSelected -> MaterialTheme.colorScheme.onTertiary
    isEnabled -> Color.Unspecified
    else -> Color.LightGray.copy(alpha = 0.6f)
}


@Composable
private fun rememberCalendarManager(
    yearMonth: YearMonth,
    selectedDate: LocalDate? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) = remember(yearMonth, selectedDate, minDate, maxDate) {
    CalendarManager(
        yearMonth = yearMonth,
        selectedDate = selectedDate,
        minDate = minDate,
        maxDate = maxDate,
    )
}

@Stable
class CalendarManager(
    yearMonth: YearMonth,
    private val selectedDate: LocalDate? = null,
    private val minDate: LocalDate? = null,
    private val maxDate: LocalDate? = null
) {
    private val firstDayOfWeek = LocalDate.of(yearMonth.year, yearMonth.month, 1).dayOfWeek
    private val days = initializeDays(yearMonth)

    fun getCalendarMonth(): CalendarMonth {
        val weekSize = 7
        val firstWeek = getDaysOfFirstWeek(days, firstDayOfWeek)
        val offsetAfterFirstWeek = firstWeek.count { it != null }
        val otherWeeks = getDaysOfWeek(days, offsetAfterFirstWeek)
            .insertNullIfNotFull(weekSize)
        val allWeeks = listOf(firstWeek, *otherWeeks.toTypedArray())
        val calendarWeeks = allWeeks.map { week ->
            week.map { day ->
                day?.toDisplayDay(selectedDate, minDate, maxDate)
            }
        }
        return CalendarMonth(calendarWeeks)
    }


    /**
     * Generates a set of [Day] for a month based on the provided [yearMonth] value.
     */
    private fun initializeDays(yearMonth: YearMonth): Set<Day> {
        val days = mutableSetOf<Day>()
        for (dayOfMonth in 1..yearMonth.lengthOfMonth()) {
            days.add(
                Day(date = LocalDate.of(yearMonth.year, yearMonth.month, dayOfMonth))
            )
        }
        return days
    }

    /**
     * If the size of the last child (a list of [Day]) of a list is smaller than [requiredSize],
     * then null items will be inserted in that last child in order to have the [requiredSize].
     */
    private fun List<List<Day?>>.insertNullIfNotFull(requiredSize: Int): List<List<Day?>> {
        return if (last().size == requiredSize) {
            this
        } else {
            val lists = this.toMutableList()
            val lastList = lists.removeLast().toMutableList()
            val numberOfMissingDays = requiredSize - lastList.size
            val missingItems = List(numberOfMissingDays) { null }
            lastList.addAll(missingItems)
            lists.add(lastList)
            lists
        }
    }

    /**
     * Generates a list of [List]s of type [Day] that represent the weeks of the month
     * except the first week. For generating the first week of a month use [getDaysOfFirstWeek].
     *
     * @param days a set of [Day] that represent all days of a month;
     * @param offset a number of the days that should be dropped from the [days] set
     * because those are in boundaries of the first week of a month.
     */
    private fun getDaysOfWeek(days: Set<Day>, offset: Int): List<List<Day?>> {
        val weekSize = 7
        var daysList = days.drop(offset)
        val result = mutableListOf<List<Day?>>()
        while (daysList.isNotEmpty()) {
            val week = daysList.take(weekSize)
            result.add(week)
            daysList = daysList.drop(weekSize)
        }
        return result.toList()
    }


    /**
     * Generates a list of a nullable [Day] that represent the first week of a month.
     * @param days a set of [Day] that represent all days of a month;
     * @param dayOfWeekOfFirstDayOfMonth a [DayOfWeek] value that denotes what day of week
     * the first day of the month is.
     */
    private fun getDaysOfFirstWeek(
        days: Set<Day>,
        dayOfWeekOfFirstDayOfMonth: DayOfWeek
    ): List<Day?> {
        val totalNumberOfDaysOfWeek = 7
        // E.g., if it's Friday (value = 5),
        // then a number of days of the first week is 3 (7-5+1; +1 because of including Friday).
        val numberOfDaysOfWeek = totalNumberOfDaysOfWeek - dayOfWeekOfFirstDayOfMonth.value + 1
        // Take the number of days from the days of the month.
        val daysOfWeek = days.take(numberOfDaysOfWeek).toMutableList()
        // Create a CalendarWeek starting from the last day because it's the first week of month
        // and it can have empty null values instead of a DisplayDay from the beginning of a week
        // but not from the end (from the end can be in the last CalendarWeek).
        return List(totalNumberOfDaysOfWeek) {
            daysOfWeek.removeLastOrNull() // 1st - Sunday ... 7th - Monday
        }.asReversed() // 1st - Monday ... 7th - Sunday
    }


    /**
     * Converts [Day] to [DisplayDay] applying the provided parameters.
     * @param selectedDate the current select date;
     * @param minDate the minimal date that can be selected;
     * @param maxDate the maximal date that can be selected.
     *
     * @receiver [Day]
     * @return [DisplayDay]
     */
    private fun Day.toDisplayDay(
        selectedDate: LocalDate?,
        minDate: LocalDate?,
        maxDate: LocalDate?
    ) = DisplayDay(
        day = this,
        isSelected = this.date == selectedDate,
        isEnabled = this.isEnabled(minDate, maxDate)
    )

    /**
     * Returns true if the receiver [Day] is within the [minDate] and [maxDate].
     * @param minDate
     * @param maxDate
     * @receiver [Day]
     * @return [Boolean]
     */
    private fun Day.isEnabled(minDate: LocalDate?, maxDate: LocalDate?): Boolean {
        return when {
            minDate == null && maxDate == null -> true
            minDate != null && date.isBefore(minDate) -> false
            maxDate != null && date.isAfter(maxDate) -> false
            else -> true
        }
    }
}

/**
 *A wrapper class for a list of weeks (list of a nullable [DisplayDay]) of a month.
 */
data class CalendarMonth(val weeks: List<List<DisplayDay?>>)

/**
 * A wrapper class for [LocalDate].
 * @property date
 * @property dayOfMonth the ordinal value of the [date] in the month of the [date]
 */
data class Day(
    val date: LocalDate
) {
    val dayOfMonth = date.dayOfMonth
}

/**
 * A class that contains state for the provided [day].
 *
 * @param day a [Day] instance;
 * @param isSelected a flag to show that this [day] is currently selected by user;
 * @param isEnabled a flag to show that this [day] can be selected by user;
 * @property isToday a flag to show that this [day] is today.
 */
data class DisplayDay(
    val day: Day,
    val isSelected: Boolean,
    val isEnabled: Boolean
) {
    val isToday = day.date == LocalDate.now()
}
