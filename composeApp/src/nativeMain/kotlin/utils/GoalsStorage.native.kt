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

    override suspend fun saveStepsProgress(progress: Int) {
        NSUserDefaults.standardUserDefaults.setInteger(progress.toLong(), forKey = "stepsProgress")
    }

    override suspend fun saveExerciseProgress(progress: Int) {
        NSUserDefaults.standardUserDefaults.setInteger(progress.toLong(), forKey = "exerciseProgress")
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun loadGoals(): Goals = memScoped {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val stepsGoal = alloc<NSIntegerVar>().apply { value = userDefaults.integerForKey("stepsGoal") }
        val exerciseGoal = alloc<NSIntegerVar>().apply { value = userDefaults.integerForKey("exerciseGoal") }
        Goals(stepsGoal.value.toInt(), exerciseGoal.value.toInt())
    }

    override suspend fun loadStepsProgress(): Int {
        return NSUserDefaults.standardUserDefaults.integerForKey("stepsProgress").toInt()
    }

    override suspend fun loadExerciseProgress(): Int {
        return NSUserDefaults.standardUserDefaults.integerForKey("exerciseProgress").toInt()
    }
}

actual fun getGoalsStorageInstance(context: PlatformContext): IGoalsStorage = IOSGoalsStorage()
