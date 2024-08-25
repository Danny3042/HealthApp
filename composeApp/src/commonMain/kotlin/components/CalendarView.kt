package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.math.roundToInt

data class CalendarUiModel(
    val selectedDate: Date,
    val visibleDates: List<Date>,
    val events: List<Event> = listOf(),
    val ratings: Map<LocalDate, Rating> = emptyMap()
) {
    val startDate: Date = visibleDates.first()
    val endDate: Date = visibleDates.last()

    data class Date(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean
    ) {
        val day: String = date.dayOfWeek.name.take(3)
    }
}

data class Event(
    val date: LocalDate,
    val title: String
)

data class Rating(
    val sleepRating: Float,
    val moodRating: Float
)

class CalendarDataSource {
    val today: LocalDate
        get() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    var events: MutableList<Event> = mutableListOf()

    fun getData(startDate: LocalDate = today, lastSelectedDate: LocalDate): CalendarUiModel {
        val firstDayOfWeek = startDate
        val endDayOfWeek = firstDayOfWeek.plus(7, DateTimeUnit.DAY)
        val visibleDates = getDatesBetween(firstDayOfWeek, endDayOfWeek)
        return toUiModel(visibleDates, lastSelectedDate)
    }

    fun addEvent(event: Event) {
        events.add(event)
    }

    private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        return generateSequence(startDate) { it.plus(1, DateTimeUnit.DAY) }
            .takeWhile { it <= endDate }
            .toList()
    }

    private fun toUiModel(dateList: List<LocalDate>, lastSelectedDate: LocalDate): CalendarUiModel {
        return CalendarUiModel(
            selectedDate = toItemUiModel(lastSelectedDate, true),
            visibleDates = dateList.map { toItemUiModel(it, it == lastSelectedDate) },
        )
    }

    private fun toItemUiModel(date: LocalDate, isSelected: Boolean) = CalendarUiModel.Date(
        isSelected = isSelected,
        isToday = date == today,
        date = date,
    )
}

@Composable
fun Header(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
    onTodayClickListener: () -> Unit,
) {
    Row {
        Text(
            text = if (data.selectedDate.isToday) {
                "Today"
            } else {
                data.selectedDate.date.toString() // You might want to format this date as needed
            },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        IconButton(onClick = { onTodayClickListener() }) {
            Icon(
                imageVector = Icons.Filled.Today,
                contentDescription = "Today"
            )
        }
        IconButton(onClick = { onPrevClickListener(data.startDate.date) }) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = "Previous"
            )
        }
        IconButton(onClick = { onNextClickListener(data.endDate.date) }) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Next"
            )
        }
    }
}

@Composable
fun EventInputDialog(
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("New Event") },
        text = {
            TextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Event Title") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(input)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ScheduleView(modifier: Modifier = Modifier, dataSource: CalendarDataSource) {
    var calendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopCenter).padding(top = 54.dp)) {
            InfoCard()
            Header(
                data = calendarUiModel,
                onPrevClickListener = { startDate ->
                    val finalStartDate = startDate.minus(1, DateTimeUnit.DAY)
                    calendarUiModel = dataSource.getData(
                        startDate = finalStartDate,
                        lastSelectedDate = calendarUiModel.selectedDate.date
                    )
                },
                onNextClickListener = { endDate ->
                    val finalStartDate = endDate.plus(2, DateTimeUnit.DAY)
                    calendarUiModel = dataSource.getData(
                        startDate = finalStartDate,
                        lastSelectedDate = calendarUiModel.selectedDate.date
                    )
                },
                onTodayClickListener = {
                    calendarUiModel = dataSource.getData(lastSelectedDate = dataSource.today)
                }
            )
            Content(
                calendarUiModel = calendarUiModel,
                onDateClickListener = { date ->
                    calendarUiModel = calendarUiModel.copy(
                        selectedDate = date,
                        visibleDates = calendarUiModel.visibleDates.map {
                            it.copy(isSelected = it.date == date.date)
                        }
                    )
                },
                onUpdateCalendarUiModel = { updatedModel ->
                    calendarUiModel = updatedModel
                }
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp) // Adjust padding to place above nav bar
        ) {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }

    if (showDialog) {
        RatingInputDialog(
            onConfirm = { sleepRating, moodRating ->
                val newRatings = calendarUiModel.ratings.toMutableMap()
                newRatings[calendarUiModel.selectedDate.date] = Rating(sleepRating, moodRating)
                calendarUiModel = calendarUiModel.copy(ratings = newRatings)
                showDialog = false
            },
            onDismissRequest = { showDialog = false }
        )
    }
}



// Update the RatingCard composable to call a function that deletes the rating from the data source when swiped
@Composable
fun RatingCard(rating: Rating, onDelete: () -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    offsetX += dragAmount
                    if (offsetX > 300) { // Adjust threshold as needed
                        onDelete()
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Sleep Rating: ${rating.sleepRating}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Mood Rating: ${rating.moodRating}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Update the Content composable to remove the rating from the data source permanently
@Composable
fun Content(
    calendarUiModel: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    onUpdateCalendarUiModel: (CalendarUiModel) -> Unit
) {
    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp)
        ) {
            items(items = calendarUiModel.visibleDates) { date ->
                ContentItem(
                    date = date,
                    onClickListener = onDateClickListener
                )
            }
        }
        calendarUiModel.ratings[calendarUiModel.selectedDate.date]?.let { rating ->
            RatingCard(
                rating = rating,
                onDelete = {
                    val newRatings = calendarUiModel.ratings.toMutableMap()
                    newRatings.remove(calendarUiModel.selectedDate.date)
                    onUpdateCalendarUiModel(calendarUiModel.copy(ratings = newRatings))
                }
            )
        }
    }
}

@Composable
fun RatingInputDialog(
    onConfirm: (Float, Float) -> Unit,
    onDismissRequest: () -> Unit
) {
    var sleepRating by remember { mutableStateOf(5f) }
    var moodRating by remember { mutableStateOf(5f) }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("New Rating") },
        text = {
            Column {
                Text("Sleep Rating")
                Slider(
                    value = sleepRating,
                    onValueChange = { sleepRating = it },
                    valueRange = 0f..10f,
                    steps = 9
                )
                Text("Mood Rating")
                Slider(
                    value = moodRating,
                    onValueChange = { moodRating = it },
                    valueRange = 0f..10f,
                    steps = 9
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(sleepRating, moodRating)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ContentItem(
    date: CalendarUiModel.Date,
    onClickListener: (CalendarUiModel.Date) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable { onClickListener(date) },
        colors = CardDefaults.cardColors(
            containerColor = if (date.isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
    ) {
        Column(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .padding(4.dp)
        ) {
            Text(
                text = date.day,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = date.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Navigation Instructions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "1. Swipe left or right on a rating card to delete it.\n" +
                        "2. Click on a date to view or add ratings.\n" +
                        "3. Click on the floating action button to add a new rating.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}