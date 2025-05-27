package sub_pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmk.kmpnotifier.notification.NotifierManager
import components.TimeEditDialog
import kotlinx.coroutines.delay

@Composable
fun MeditationPage() {
    var totalTime = 5 * 60 // 5 minutes in seconds
    var timeLeft by remember { mutableStateOf(totalTime) }
    val progress = timeLeft / totalTime.toFloat()
    var isRunning by remember { mutableStateOf(false) }
    val notifier = remember { NotifierManager.getLocalNotifier() }
    var editText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
        if (timeLeft == 0) {
            isRunning = false
            notifier.notify(
                title = "Meditation Timer Finished",
                body = "Your meditation session has ended. Take a moment to reflect.",
            )
        }
    }

    if (showEditDialog) {
        TimeEditDialog(
            initialMinutes = totalTime / 60,
            initialSeconds = totalTime % 60,
            onDismiss = { showEditDialog = false },
            onConfirm = { minutes, seconds ->
                totalTime = minutes * 60 + seconds
                timeLeft = totalTime
                showEditDialog = false
                isRunning = false // Reset running state when editing time
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Meditation Timer", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "${(timeLeft / 60).toString().padStart(2, '0')}:${(timeLeft % 60).toString().padStart(2, '0')}",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.padding(16.dp),
        )
        Row {
            Button(
                onClick = { isRunning = true },
                enabled = !isRunning && timeLeft > 0
            ) { Text("Start") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { isRunning = false },
                enabled = isRunning
            ) { Text("Pause") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    isRunning = false
                    timeLeft = totalTime
                }
            ) { Text("Reset") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                editText = (totalTime / 60).toString()
                showEditDialog = true
            }
        ) { Text("Edit") }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Take a deep breath and relax.\nFocus on your breathing.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}