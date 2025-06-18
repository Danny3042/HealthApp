package utils

expect object HabitStorage {
    fun saveHabits(habits: List<String>)
    fun loadHabits(): List<String>
}