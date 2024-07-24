package utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pages.Goals

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "goals")

class AndroidGoalsStorage(private val context: Context) : IGoalsStorage {
    private val stepsGoalKey = intPreferencesKey("steps_goal")
    private val exerciseGoalKey = intPreferencesKey("exercise_goal")

    private val stepsProgressKey = intPreferencesKey("steps_progress")
    private val exerciseProgressKey = intPreferencesKey("exercise_progress")

    override suspend fun saveGoals(stepsGoal: Int, exerciseGoal: Int) {
        context.dataStore.edit { preferences ->
            preferences[stepsGoalKey] = stepsGoal
            preferences[exerciseGoalKey] = exerciseGoal
        }
    }

    override suspend fun loadGoals(): Goals {
        return context.dataStore.data.map { preferences ->
            val stepsGoal = preferences[stepsGoalKey] ?: 0
            val exerciseGoal = preferences[exerciseGoalKey] ?: 0
            Goals(stepsGoal, exerciseGoal)
        }.first()
    }

    override suspend fun saveStepsProgress(progress: Int) {
        context.dataStore.edit { preferences ->
            preferences[stepsProgressKey] = progress
        }
    }

    override suspend fun saveExerciseProgress(progress: Int) {
        context.dataStore.edit { preferences ->
            preferences[exerciseProgressKey] = progress
        }
    }

    override suspend fun loadStepsProgress(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[stepsProgressKey] ?: 0
        }.first()
    }

    override suspend fun loadExerciseProgress(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[exerciseProgressKey] ?: 0
        }.first()

    }
}

actual fun getGoalsStorageInstance(context: Context): IGoalsStorage = AndroidGoalsStorage(context)