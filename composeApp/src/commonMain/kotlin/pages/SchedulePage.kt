package pages

import calendar.CalendarDataSource
import calendar.ScheduleView
import androidx.compose.runtime.Composable

@Composable
fun SchedulePage() {
   val dataSource = CalendarDataSource()
   ScheduleView(dataSource = dataSource)
}