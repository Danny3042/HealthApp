package platform

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

expect @Composable fun rememberSafeAreaInsets(): PaddingValues
expect @Composable fun rememberSafeAreaInsetsWithTabBar(): PaddingValues
