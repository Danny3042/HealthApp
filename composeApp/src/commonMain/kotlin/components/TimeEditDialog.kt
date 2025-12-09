package components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import keyboardUtil.hideKeyboard
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun TimeEditDialog(
    initialMinutes: Int,
    initialSeconds: Int,
    onDismiss: () -> Unit,
    onConfirm: (minutes: Int, seconds: Int) -> Unit
) {
    var minutesText by remember { mutableStateOf(initialMinutes.toString()) }
    var secondsText by remember { mutableStateOf(initialSeconds.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Timer") },
        text = {
            Row {
                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it.filter { c -> c.isDigit() } },
                    label = { Text("Minutes") },
                    modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { hideKeyboard() })
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = secondsText,
                    onValueChange = { secondsText = it.filter { c -> c.isDigit() } },
                    label = { Text("Seconds") },
                    modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { hideKeyboard() })
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val min = minutesText.toIntOrNull() ?: 0
                val sec = secondsText.toIntOrNull() ?: 0
                if (min >= 0 && sec in 0..59 && (min > 0 || sec > 0)) {
                    hideKeyboard()
                    onConfirm(min, sec)
                }
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}