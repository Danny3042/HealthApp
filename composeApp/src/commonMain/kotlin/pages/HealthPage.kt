package pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.CalendarDataSource
import components.ScheduleView

@Composable
fun HealthPage() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        WeekView()
        Spacer(modifier = Modifier.height(16.dp))
        SleepRatingCard()
        Spacer(modifier = Modifier.height(16.dp))
        MoodRatingCard()
    }
}

@Composable
fun WeekView() {
    val dataSource = CalendarDataSource()
    // Add your week view content here
    ScheduleView(dataSource = dataSource)
}

@Composable
fun SleepRatingCard() {
    // Add your sleep rating card content here
    Text("Sleep Rating Card")
}

@Composable
fun MoodRatingCard() {
    // Add your mood rating card content here
    Text("Mood Rating Card")
}