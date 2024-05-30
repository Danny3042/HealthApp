package navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_AVAILABLE
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import components.InstalledMessage
import components.NotInstalledMessage
import components.NotSupportedMessage
import org.example.project.R

/**
 * Welcome screen shown when the app is first launched.
 */
@Composable
fun WelcomeScreen(
    healthConnectAvailability: Int,
    onResumeAvailabilityCheck: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val currentOnAvailabilityCheck by rememberUpdatedState(onResumeAvailabilityCheck)

    // Add a listener to re-check whether Health Connect has been installed each time the Welcome
    // screen is resumed: This ensures that if the user has been redirected to the Play store and
    // followed the onboarding flow, then when the app is resumed, instead of showing the message
    // to ask the user to install Health Connect, the app recognises that Health Connect is now
    // available and shows the appropriate welcome.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentOnAvailabilityCheck()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(0.5f),
            painter = painterResource(id = R.drawable.ic_health_connect_logo),
            contentDescription = stringResource(id = R.string.health_connect_logo)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.welcome_message),
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        when (healthConnectAvailability) {
            SDK_AVAILABLE -> InstalledMessage()
            SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> NotInstalledMessage()
            SDK_UNAVAILABLE -> NotSupportedMessage()
        }
    }
}

@Preview
@Composable
fun InstalledMessagePreview() {
    MaterialTheme {
        WelcomeScreen(
            healthConnectAvailability = SDK_AVAILABLE,
            onResumeAvailabilityCheck = {}
        )
    }
}

@Preview
@Composable
fun NotInstalledMessagePreview() {
    MaterialTheme {
        WelcomeScreen(
            healthConnectAvailability = SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED,
            onResumeAvailabilityCheck = {}
        )
    }
}

@Preview
@Composable
fun NotSupportedMessagePreview() {
    MaterialTheme {
        WelcomeScreen(
            healthConnectAvailability = SDK_UNAVAILABLE,
            onResumeAvailabilityCheck = {}
        )
    }
}