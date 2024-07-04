package components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomAppBar(onActionClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        TopAppBar(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
            title = {
                Column {
                    Text(
                        text = "ChatGemini",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    )
                    Text(
                        text = "Powered by Gemini Pro",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        onActionClick()
                    },
                    content = {
                        Icon(
                            Icons.Filled.Key,
                            contentDescription = null,
                        )
                    },
                )
            },
        )
    }
}