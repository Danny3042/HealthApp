package sub_pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import utils.HabitRepository
import utils.isAndroid

const val CompletedHabitsPageRoute = "completed_habits"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedHabitsPage(navController: NavHostController) {
    val completedHabits = HabitRepository.completedHabits
    val scrollState = rememberScrollState()

    Column {
        if (isAndroid()) {
            TopAppBar(
                title = { Text("Completed Habits") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            completedHabits.forEach { habit ->
                Text(habit, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}