package calendar


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ScheduleView(modifier: Modifier = Modifier, dataSource: CalendarDataSource) {
    var calendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }

    Column(modifier = Modifier.padding(top = 50.dp)) {
        Header(
            data = calendarUiModel,
            onPrevClickListener = { startDate ->
                val finalStartDate = startDate.minus(1, DateTimeUnit.DAY)
                calendarUiModel = dataSource.getData(startDate = finalStartDate, lastSelectedDate = calendarUiModel.selectedDate.date)
            },
            onNextClickListener = { endDate ->
                val finalStartDate = endDate.plus(2, DateTimeUnit.DAY)
                calendarUiModel = dataSource.getData(startDate = finalStartDate, lastSelectedDate = calendarUiModel.selectedDate.date)
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
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Date: ${event.date}",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }

    // DatePickerDialog and TimePickerDialog implementations here...

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(onClick = { /* Show Date Range Picker Dialog */ }) {
            Icon(Icons.Filled.Add, contentDescription = "Add Event")
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
        backgroundColor = if (date.isSelected) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.secondary
        }
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
                    backgroundColor = MaterialTheme.colors.secondary
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Text(
                text = date.day,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.body2
            )
            Text(
                text = date.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.body1,
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



