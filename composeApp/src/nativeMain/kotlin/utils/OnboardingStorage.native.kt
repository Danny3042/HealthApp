package utils
import platform.Foundation.NSUserDefaults

actual class OnboardingStorage {
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    actual fun isOnboardingCompleted(): Boolean =
        defaults.boolForKey("completed")

    actual fun setOnboardingCompleted(completed: Boolean) {
        defaults.setBool(completed, forKey = "completed")
    }
}