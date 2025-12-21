import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    println("App(): PlatformApp will be invoked")
    PlatformApp()
}

// Platform-specific entry point. Implementations should live in androidMain and iosMain.
expect @Composable fun PlatformApp()
