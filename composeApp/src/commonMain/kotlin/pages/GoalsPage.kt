package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sub_pages.HabitTrackerPage
import sub_pages.MeditationPage

@Composable
fun GoalsPage() {
    var selectedMiniApp by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Goals", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Mini-app cards
        MiniAppCard("Meditation") { selectedMiniApp = "Meditation" }
        Spacer(modifier = Modifier.height(16.dp))
        MiniAppCard("Habit Tracker") { selectedMiniApp = "HabitTracker" }
        // Add more mini-apps here

        Spacer(modifier = Modifier.height(32.dp))

        // Show selected mini-app
        when (selectedMiniApp) {
            "Meditation" -> MeditationPage()
            "HabitTracker" -> HabitTrackerPage()
            // Add more mini-apps here
        }
    }
}

@Composable
fun MiniAppCard(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title)
    }
}

