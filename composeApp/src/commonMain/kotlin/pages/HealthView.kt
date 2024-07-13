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
import androidx.lifecycle.viewmodel.compose.viewModel
import model.HealthViewModel
import kotlin.math.roundToInt

@Composable
fun ExpandableCard(title: String, onSave: (Float) -> Unit){
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
                SliderExample(sliderValue, onSave = { value -> sliderValue = value.toString(); onSave(value) })
            } else {
                Text(text = sliderValue)
            }
        }
    }
}

@Composable
fun DescriptionCard() {
    var showDescription by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { showDescription = !showDescription },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Expandable Cards Info", style = MaterialTheme.typography.h6)
            if (showDescription) {
                Text(text = "This app uses expandable cards to rate your sleep and mood. " +
                        "Slide to select a value and hit save to update your ratings.")
            }
        }
    }
}


@Composable
fun SliderExample(currentValue: String, onSave: (Float) -> Unit) {
    val sliderLabels = (1..10).map { it.toString() } // Labels from 1 to 10
    var sliderPosition by remember { mutableStateOf(
        if (sliderLabels.contains(currentValue)) sliderLabels.indexOf(currentValue).toFloat() else 0f
    )}
    Column {
        Text("1 - Worst, 10 - Best", style = MaterialTheme.typography.caption)
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onSave(sliderLabels[sliderPosition.roundToInt()].toFloat()) // Convert position to value and pass it
            },
            valueRange = 0f..(sliderLabels.size - 1).toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.secondary,
                activeTrackColor = MaterialTheme.colors.secondary,
                inactiveTrackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            ),
            steps = 9 // 10 - 1 = 9 steps for values 1 to 10
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
fun HealthView(onNavigateToTimerView: () -> Unit) {
    val viewModel: HealthViewModel = viewModel()

    Column {

        DescriptionCard()
        // Sleep Rating Card
        ExpandableCard("Sleep Rating") { value ->
            viewModel.updateSleepRating(value)
        }

        // Mood Rating Card
        ExpandableCard("Mood Rating") { value ->
            viewModel.updateMoodRating(value)
        }



        // Check for dialog condition
        if (viewModel.showDialog) {
            AlertDialogExample(
                onDismissRequest = { viewModel.showDialog = false },
                onConfirmation = {
                    println("Navigating to TimerView")
                    onNavigateToTimerView()
                    viewModel.showDialog = false
                },
                dialogTitle = "Meditation Request",
                dialogText = "Based on the information we suggest to start a meditation session."
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