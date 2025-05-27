package sub_pages
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

const val DarkModeSettingsPageScreen = "DarkModeSettings"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkModeSettingsPage(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    useSystemDefault: Boolean,
    onUseSystemDefaultToggle: (Boolean) -> Unit,
    navController: NavController? = null // Optional for back navigation
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Use System Default")
            Spacer(modifier = Modifier.height(8.dp))
            Switch(
                checked = useSystemDefault,
                onCheckedChange = onUseSystemDefaultToggle
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Enable Dark Mode")
            Spacer(modifier = Modifier.height(8.dp))
            Switch(
                checked = isDarkMode,
                onCheckedChange = onDarkModeToggle,
                enabled = !useSystemDefault
            )
        }
    }
}