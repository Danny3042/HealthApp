package components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

data class CalendarUiModel(
    val selectedDate: Date,
    val visibleDates: List<Date>,
    val events: List<Event> = listOf()
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
    onDismissRequest: () -> Unit) {
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {onDismissRequest() },
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
                onClick = {onDismissRequest() }

            ) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleView(modifier: Modifier = Modifier, dataSource: CalendarDataSource) {
    var calendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        EventInputDialog(
            onConfirm = { title ->
                dataSource.addEvent(Event(date = dataSource.today, title = title))
                showDialog = false
            },
            onDismissRequest = { showDialog = false }
        )
    }

    Column(modifier = Modifier.padding(top = 50.dp)) {
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
            data = calendarUiModel,
            onDateClickListener = { date ->
                calendarUiModel = calendarUiModel.copy(
                    selectedDate = date,
                    visibleDates = calendarUiModel.visibleDates.map {
                        it.copy(isSelected = it.date == date.date)
                    }
                )
            }
        )
        val selectedDate = calendarUiModel.selectedDate.date

        LazyColumn {
            items(dataSource.events.filter { it.date == selectedDate }) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Event: ${event.title}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Date: ${event.date}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    // DatePickerDialog and TimePickerDialog implementations here...

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent =  {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Mental Health Wellbeing Questionnaire", Modifier.padding(16.dp))
            }
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f),
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Event")
            }
        }
    }
}

@Composable
fun ContentItem(
    date: CalendarUiModel.Date,
    events: List<Event>,
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
                .width(40.dp)
                .height(48.dp)
                .padding(4.dp)
        ) {
            for (event in events) {
                Card(
                    modifier = Modifier.padding(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
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
fun Content(
    data: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp)
    ) {
        items(items = data.visibleDates) { date ->
            ContentItem(
                date = date,
                events = data.events.filter { it.date == date.date },
                onClickListener = onDateClickListener
            )
        }
    }
}



