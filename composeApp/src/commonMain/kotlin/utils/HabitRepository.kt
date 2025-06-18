package utils


object HabitRepository {
    var completedHabits = HabitStorage.loadHabits().toMutableList()

    fun addCompletedHabit(habit: String) {
        if (!completedHabits.contains(habit)) {
            completedHabits.add(habit)
            HabitStorage.saveHabits(completedHabits)
        }
    }
}