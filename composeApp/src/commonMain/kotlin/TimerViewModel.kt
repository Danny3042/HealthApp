import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel: ViewModel() {
    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timer.value > 0) {
                delay(1000)
                _timer.value--
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
    }

    fun setTimer(time: Long) {
        _timer.value = time
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timer.value = 0
    }
}