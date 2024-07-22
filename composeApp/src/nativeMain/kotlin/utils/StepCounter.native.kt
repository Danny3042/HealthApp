package utils

import platform.CoreMotion.CMPedometer
import platform.Foundation.NSDate
import platform.Foundation.NSOperationQueue

actual abstract class PlatformContext
actual class StepCounter actual constructor(context: PlatformContext) {
    private val pedometer = CMPedometer()
    private val operationQueue = NSOperationQueue()

    actual var stepCount: Int = 0
    actual fun startListening() {
        pedometer.startPedometerUpdatesFromDate(NSDate(),) { pedometerData, error ->
            stepCount = pedometerData?.numberOfSteps?.intValue ?: 0
        }
    }

    actual fun stopListening() {
        pedometer.stopPedometerUpdates()
    }

}