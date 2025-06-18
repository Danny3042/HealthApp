package pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.CalendarDataSource


@Composable
fun HealthPage(onNavigateToTimerView: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        WeekView(onNavigateToTimerView = onNavigateToTimerView)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun WeekView(onNavigateToTimerView: () -> Unit) {
    val dataSource = CalendarDataSource()
    // Add your week view content here
    ScheduleView(dataSource = dataSource)
}






