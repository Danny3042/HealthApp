package components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.Status

@Composable
fun CustomBottomBar(
    modifier: Modifier = Modifier,
    status: Status,
    onSendClick: (String, List<ByteArray>) -> Unit
) {
    val textState = remember { mutableStateOf("") }
    val images = remember { mutableStateOf(listOf<ByteArray>()) }

    val scope = rememberCoroutineScope()
    Column {
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            maxLines = 3,
            placeholder = {
                Text(
                    text = "Type a message...",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                    ),
                    textAlign = TextAlign.Center
                )
            },
            trailingIcon = {
                Button(
                    onClick = {
                        onSendClick(textState.value, images.value)
                        images.value = emptyList()
                        textState.value = ""
                    },
                    enabled = textState.value.isNotBlank() && status != Status.Loading,
                    content = {
                        if (status is Status.Loading)
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                            )
                        else {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.rotate(-90.0F).size(20.dp),
                            )
                        }
                    },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            },
            modifier = modifier,
            shape = RoundedCornerShape(24),
        )
    }
}