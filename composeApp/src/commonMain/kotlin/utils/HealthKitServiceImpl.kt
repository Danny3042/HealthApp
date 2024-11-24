package utils

import kotlinx.coroutines.flow.Flow

interface HealthKitManager {
    fun requestAuthorization(): Boolean
    fun checkPermissions(): Boolean
    fun readData(): Flow<HealthData>
}

class HealthKitServiceImpl(private val healthKitManager: iOSHealthKitManager) : HealthKitService {

    override fun requestAuthorization(): Boolean {
        return healthKitManager.requestAuthorization()
    }

    override fun checkPermissions(): Boolean {
        return healthKitManager.checkPermissions()
    }

    override fun readData(): Flow<HealthData> {
        return healthKitManager.readData()
    }
}