package utils

import kotlinx.coroutines.flow.Flow

interface HealthKitManager {
    fun requestAuthorization()
    fun readData(): Flow<HealthData>
}

class HealthKitServiceImpl(private val healthKitManager: iOSHealthKitManager) : HealthKitService {

    override fun requestAuthorization() {
        healthKitManager.requestAuthorization()
    }

    override fun readData(): Flow<HealthData> {
        return healthKitManager.readData()
    }
}