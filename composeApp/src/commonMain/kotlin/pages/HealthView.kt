package pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ExpandableCard(title: String, onSave: (String) -> Unit){
    var expanded by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(
                onClick = { expanded = !expanded }
            )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Expand or collapse content",
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                }
            }
            if (expanded) {
                SliderExample(sliderValue, onSave = { value -> sliderValue = value; onSave(value) })
            } else {
                Text(text = sliderValue)
            }
        }
    }
}

@Composable
fun SliderExample(currentValue: String, onSave : (String) -> Unit) {
    val sliderLabels = (1..10).map { it.toString() } // Create labels from 1 to 10
    var sliderPosition by remember { mutableStateOf(
        if (sliderLabels.contains(currentValue)) sliderLabels.indexOf(currentValue).toFloat() else 0f
    )}
    Column {
        Text("1 - Worst, 10 - Best", style = MaterialTheme.typography.caption)
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 0f..(sliderLabels.size - 1).toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.secondary,
                activeTrackColor = MaterialTheme.colors.secondary,
                inactiveTrackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            ),
            steps = 10, // Set steps to 10
            modifier = Modifier.padding(16.dp)
        )
        Text(text = sliderLabels[sliderPosition.roundToInt()])
    }
}

@Composable
fun MyButton(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(60.dp)
        ) {
            Button(
                onClick = onClick,

                ) {
                Text("Meditation", color = MaterialTheme.colors.onPrimary)
            }
        }
    }
}


@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        confirmButton = {
            Button(
                onClick = {
                    println("AlertDialogExample: Confirm button clicked")
                    onConfirmation()
                    onDismissRequest() },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
            ) {
                Text("Confirm", color = MaterialTheme.colors.onPrimary)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDismissRequest() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.secondary)
            ) {
                Text("Dismiss")
            }
        }
    )
}
@Composable
fun HealthView(onNavigateToTimerView : () -> Unit) {
    var sleepRating by remember { mutableStateOf(5f) }
    var painRating by remember { mutableStateOf(5f) }
    var showDialog by remember { mutableStateOf(false) }
    Column {
       ExpandableCard("Sleep Rating") { value -> sleepRating = value.toFloat() }
        ExpandableCard("Mood Rating") { value -> println("Mood rating: $value") }

        MyButton(onClick = { showDialog = true })
        if(showDialog) {
            AlertDialogExample(
                onDismissRequest = { showDialog = false },
                onConfirmation = {
                    println("Navigating to TimerView")
                    onNavigateToTimerView(); showDialog = false },
                dialogTitle = "Meditation Request",
                dialogText = "Based on the information we suggest to start a meditation session: ${sleepRating.toInt()} sleep rating and ${painRating.toInt()} pain rating"
            )
        }
    }
}


@Composable
fun HealthViewScreen() {
    var currentScreen by remember { mutableStateOf("HealthView") }
    when (currentScreen) {
        "HealthView" -> HealthView { currentScreen = "TimerView" }
        "TimerView" -> TimerScreenContent(onBack = { currentScreen = "HealthView" })
    }
}