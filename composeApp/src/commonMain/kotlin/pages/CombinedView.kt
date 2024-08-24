package pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CombinedView() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val boxWidth = maxWidth * 0.8f
        val boxHeight = maxHeight * 0.4f

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(boxWidth)
                .height(boxHeight)
        ) {
            SchedulePage()
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 20.dp)
                .width(boxWidth)
                .height(boxHeight)
        ) {
            HealthViewScreen()
        }
    }
}
