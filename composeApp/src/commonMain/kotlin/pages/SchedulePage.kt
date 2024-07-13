

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import components.CalendarDataSource
import components.Content
import components.Event
import components.EventInputDialog
import components.Header
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import model.HealthViewModel

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
}

@Composable
fun SchedulePage(healthViewComposable : @Composable () -> Unit) {
   val viewModel: HealthViewModel = viewModel()
   var selectedDay by remember { mutableStateOf(0) }
   val dataSource = CalendarDataSource()

   Column {
      ScheduleView(dataSource = dataSource)

      healthViewComposable()

      // This is a placeholder for your logic to display cards based on the selected day
      println("Displaying cards for day: $selectedDay")
   }
}