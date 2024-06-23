package utils

import kotlinx.coroutines.flow.Flow

expect class iOSHealthKitManager() : HealthKitService {
    override fun requestAuthorization()
    override fun readData(): Flow<HealthData>
}