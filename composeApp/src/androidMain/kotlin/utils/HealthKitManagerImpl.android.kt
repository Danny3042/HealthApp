package utils

import kotlinx.coroutines.flow.Flow

actual interface HealthKitService {
    actual fun requestAuthorization()
    actual fun readData(): Flow<HealthData>
}