package pages

import androidx.compose.runtime.Composable
import components.CalendarDataSource
import components.ScheduleView

@Composable
fun SchedulePage() {
   val dataSource = CalendarDataSource()
   ScheduleView(dataSource = dataSource)
}