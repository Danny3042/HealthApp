import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    println("MainViewController: Compose content mounting")
    App()
}
