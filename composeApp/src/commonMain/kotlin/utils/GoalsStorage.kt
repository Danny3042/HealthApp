package utils

import pages.Goals

interface IGoalsStorage {
    suspend fun saveGoals(stepsGoal: Int, exerciseGoal: Int)
    suspend fun loadGoals(): Goals
}

expect fun getGoalsStorageInstance(context: PlatformContext): IGoalsStorage