package utils

import kotlinx.coroutines.flow.Flow

actual class iOSHealthKitManager actual constructor() : HealthKitService {
    actual override fun requestAuthorization(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun checkPermissions(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun readData(): Flow<HealthData> {
        TODO("Not yet implemented")
    }
}