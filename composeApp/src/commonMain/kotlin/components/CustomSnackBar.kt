package components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CustomSnackBar(
    data: SnackbarData,
    contentColor: Color = Color.White,
) {
    Snackbar(
        contentColor = contentColor,
        shape = RoundedCornerShape(20),
        snackbarData = data
    )
}