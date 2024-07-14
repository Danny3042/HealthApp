package pages

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import components.CalendarDataSource
import components.ScheduleView


@Composable
fun SchedulePage() {
   val dataSource = CalendarDataSource()

   Column {
      ScheduleView(dataSource = dataSource)

   }
}