package pages

import Calendar.CalendarDataSource
import Calendar.ScheduleView
import androidx.compose.runtime.Composable

@Composable
fun SchedulePage() {
   val dataSource = CalendarDataSource()
   ScheduleView(dataSource = dataSource)
}