package utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.value
import pages.Goals
import platform.Foundation.NSUserDefaults
import platform.darwin.NSIntegerVar

class IOSGoalsStorage : IGoalsStorage {
    override suspend fun saveGoals(stepsGoal: Int, exerciseGoal: Int) {
        val userDefaults = NSUserDefaults.standardUserDefaults
        userDefaults.setInteger(stepsGoal.toLong(), forKey = "stepsGoal")
        userDefaults.setInteger(exerciseGoal.toLong(), forKey = "exerciseGoal")
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun loadGoals(): Goals = memScoped {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val stepsGoal = alloc<NSIntegerVar>().apply { value = userDefaults.integerForKey("stepsGoal") }
        val exerciseGoal = alloc<NSIntegerVar>().apply { value = userDefaults.integerForKey("exerciseGoal") }
        Goals(stepsGoal.value.toInt(), exerciseGoal.value.toInt())
    }
}

actual fun getGoalsStorageInstance(context: PlatformContext): IGoalsStorage = IOSGoalsStorage()