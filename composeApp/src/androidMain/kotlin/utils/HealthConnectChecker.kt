package utils

import androidx.health.connect.client.HealthConnectClient
import com.vitoksmile.kmp.health.HealthManagerFactory

actual object HealthConnectChecker {
    actual fun checkHealthConnectAvailability(): Int {
        val healthManager = HealthManagerFactory().createManager()
        val isAvailableResult = healthManager.isAvailable()

        return if (isAvailableResult.getOrNull() == true) {
            HealthConnectClient.SDK_AVAILABLE
        } else {
            HealthConnectClient.SDK_UNAVAILABLE
        }
    }

}