package utils

import pages.Goals

interface IGoalsStorage {
    suspend fun saveGoals(stepsGoal: Int, exerciseGoal: Int)
    suspend fun loadGoals(): Goals
    // Define methods for saving and loading progress
    suspend fun saveStepsProgress(progress: Int)
    suspend fun saveExerciseProgress(progress: Int)
    suspend fun loadStepsProgress(): Int
    suspend fun loadExerciseProgress(): Int
}

expect fun getGoalsStorageInstance(context: PlatformContext): IGoalsStorage