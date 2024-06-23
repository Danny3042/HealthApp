package utils

// In your shared code
expect abstract class PlatformContext

expect class StepCounter(context: PlatformContext) {
    fun startListening()
    fun stopListening()
    var stepCount: Int
}
