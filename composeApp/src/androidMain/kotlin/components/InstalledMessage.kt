package components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import org.example.project.R
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Welcome text shown when the app first starts, where the Healthcore APK is already installed.
 */
@Composable
fun InstalledMessage() {
    Text(
        text = stringResource(id = R.string.installed_welcome_message),
        textAlign = TextAlign.Justify
    )
}
@Preview
@Composable
fun InstalledMessagePreview() {
    MaterialTheme {
        InstalledMessage()
    }
}