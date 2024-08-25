package pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.CalendarDataSource
import components.ScheduleView

@Composable
fun HealthPage(onNavigateToTimerView: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        WeekView(onNavigateToTimerView = onNavigateToTimerView)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToTimerView,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Start Meditation Timer", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun WeekView(onNavigateToTimerView: () -> Unit) {
    val dataSource = CalendarDataSource()
    // Add your week view content here
    ScheduleView(dataSource = dataSource)
}

@Composable
fun HealthPageView(onNavigateToTimerView: () -> Unit) {
    HealthPage(onNavigateToTimerView = onNavigateToTimerView)
}


