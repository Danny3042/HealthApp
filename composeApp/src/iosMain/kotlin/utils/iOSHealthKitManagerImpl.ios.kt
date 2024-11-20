package utils

import kotlinx.coroutines.flow.Flow

actual class iOSHealthKitManager actual constructor() : HealthKitService {
    private val healthKitServiceImpl = IOSHealthKitServiceImpl()

    actual override fun requestAuthorization(): Boolean {
        return healthKitServiceImpl.requestAuthorization()
    }

    actual override fun checkPermissions(): Boolean {
        return healthKitServiceImpl.checkPermissions()
    }

    actual override fun readData(): Flow<HealthData> {
        return healthKitServiceImpl.readData()
    }


}