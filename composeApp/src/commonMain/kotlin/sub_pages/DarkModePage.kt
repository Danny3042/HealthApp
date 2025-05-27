
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


const val DarkModeSettingsPageScreen = "DarkModeSettingsScreen"


@Composable
fun PhoneIllustrationLarge(bgColor: Color) {
    Box(
        modifier = Modifier
            .size(width = 160.dp, height = 280.dp)
            .background(bgColor, shape = RoundedCornerShape(32.dp))
            .padding(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkModeSettingsPage(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    useSystemDefault: Boolean,
    onUseSystemDefaultToggle: (Boolean) -> Unit,
    navController: NavController? = null
) {
    // Determine the illustration color based on selection
    val phoneBgColor = when {
        useSystemDefault -> MaterialTheme.colorScheme.surfaceVariant
        isDarkMode -> Color(0xFF222222)
        else -> Color(0xFFF5F5F5)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TopAppBar(
            title = { Text("Dark Mode Settings") },
            navigationIcon = {
                navController?.let {
                    IconButton(onClick = { it.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Checkboxes row at the top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Checkbox(
                    checked = useSystemDefault,
                    onCheckedChange = { onUseSystemDefaultToggle(true); onDarkModeToggle(false) }
                )
                Icon(Icons.Default.Settings, contentDescription = "System Default")
                Text("System", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Checkbox(
                    checked = !useSystemDefault && !isDarkMode,
                    onCheckedChange = { onUseSystemDefaultToggle(false); onDarkModeToggle(false) }
                )
                Icon(Icons.Default.Brightness7, contentDescription = "Light Mode")
                Text("Light", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Checkbox(
                    checked = !useSystemDefault && isDarkMode,
                    onCheckedChange = { onUseSystemDefaultToggle(false); onDarkModeToggle(true) }
                )
                Icon(Icons.Default.Brightness2, contentDescription = "Dark Mode")
                Text("Dark", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        // Large phone illustration in the center
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            PhoneIllustrationLarge(bgColor = phoneBgColor)
        }
    }
}