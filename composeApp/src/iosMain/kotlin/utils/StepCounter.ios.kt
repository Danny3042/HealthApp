package utils

import platform.CoreMotion.CMPedometer
import platform.Foundation.NSDate
import platform.Foundation.NSOperationQueue

class StepCounter(private val context: PlatformContext) {
    private val pedometer = CMPedometer()
    private val operationQueue = NSOperationQueue()

    var stepCount: Int = 0
    private var stepGoal: Int = 0
    private var onGoalAchieved: ((Int) -> Unit)? = null

    fun startListening(stepsGoal: Int, onGoalAchieved: (Int) -> Unit) {
        this.stepGoal = stepsGoal
        this.onGoalAchieved = onGoalAchieved
        pedometer.startPedometerUpdatesFromDate(NSDate()) { pedometerData, error ->
            stepCount = pedometerData?.numberOfSteps?.intValue ?: 0
            if (stepCount >= stepGoal) {
                this.onGoalAchieved?.invoke(stepCount)
            }
        }
    }

    fun stopListening() {
        pedometer.stopPedometerUpdates()
    }
}
