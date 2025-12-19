package platform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Mutable holder for platform requests. HeroScreen will observe this to switch tabs when requested by native iOS.
object PlatformBridge {
    // Title of tab requested (e.g., "Habits", "Home", "Chat", "Meditate", "Profile").
    var requestedTab: String? by mutableStateOf(null)
}

