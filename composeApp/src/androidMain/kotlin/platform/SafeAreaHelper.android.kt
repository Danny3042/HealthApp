package platform

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
actual fun rememberSafeAreaInsets(): PaddingValues {
    return PaddingValues(0.dp)
}

@Composable
actual fun rememberSafeAreaInsetsWithTabBar(): PaddingValues {
    return PaddingValues(0.dp)
}
