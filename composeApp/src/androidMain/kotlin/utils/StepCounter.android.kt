package utils

// In your Android-specific code
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log


actual typealias PlatformContext = Context
actual class StepCounter actual constructor(private val context: PlatformContext) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    actual var stepCount = 0
    private var stepGoal: Int = 0
    private var onGoalAchieved: (() -> Unit)? = null

    actual fun startListening(stepsGoal: Int, onGoalAchieved: () -> Unit) {
        this.stepGoal = stepGoal
        this.onGoalAchieved = onGoalAchieved
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    actual fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            stepCount = event.values[0].toInt()
            if (stepCount >= stepGoal) {
                onGoalAchieved?.invoke()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Handle changes in sensor accuracy if needed
        when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
                // Sensor has high accuracy
                Log.d("SensorAccuracy", "Sensor has high accuracy")
            }
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                // Sensor has medium accuracy
                Log.d("SensorAccuracy", "Sensor has medium accuracy")
            }
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                // Sensor has low accuracy
                Log.d("SensorAccuracy", "Sensor has low accuracy")
            }
            SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                // Sensor is unreliable
                Log.d("SensorAccuracy", "Sensor is unreliable")
            }
        }
    }
}