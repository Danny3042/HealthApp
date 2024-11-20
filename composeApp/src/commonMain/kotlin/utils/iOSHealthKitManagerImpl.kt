package utils

import kotlinx.coroutines.flow.Flow

expect class iOSHealthKitManager() : HealthKitService {
    override fun requestAuthorization(): Boolean
    override fun checkPermissions(): Boolean
    override fun readData(): Flow<HealthData>

}