package utils

import kotlinx.coroutines.flow.Flow

actual interface HealthKitService {
    actual fun requestAuthorization(): Boolean
    actual fun checkPermissions(): Boolean
    actual fun readData(): Flow<HealthData>
}