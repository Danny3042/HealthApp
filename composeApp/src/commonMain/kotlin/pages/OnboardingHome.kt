package pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import utils.HealthKitService
import utils.OnboardingStorage

const val OnboardingHomePageScreen = "OnboardingHomePage"
@Composable
fun HomePageWithOnboarding(
    healthKitService: HealthKitService,
    onboardingRepository: OnboardingStorage
) {
    var showOnboarding by remember { mutableStateOf(!onboardingRepository.isOnboardingCompleted()) }

    if (showOnboarding) {
        OnboardingPage {
            onboardingRepository.setOnboardingCompleted(true)
            showOnboarding = false
        }
    } else {
        HomePage(healthKitService)
    }
}