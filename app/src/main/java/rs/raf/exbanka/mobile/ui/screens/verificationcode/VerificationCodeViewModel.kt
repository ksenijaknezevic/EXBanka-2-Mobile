package rs.raf.exbanka.mobile.ui.screens.verificationcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VerificationCodeUiState(
    val code: String = "",
    val remainingSeconds: Int = 300,
    val isExpired: Boolean = false
)

@HiltViewModel
class VerificationCodeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationCodeUiState())
    val uiState: StateFlow<VerificationCodeUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    fun initialize(code: String, expiresInSeconds: Int) {
        _uiState.value = VerificationCodeUiState(
            code = code,
            remainingSeconds = expiresInSeconds,
            isExpired = false
        )
        startCountdown(expiresInSeconds)
    }

    private fun startCountdown(totalSeconds: Int) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1_000L)
                remaining--
                _uiState.value = _uiState.value.copy(remainingSeconds = remaining)
            }
            // Code has expired
            _uiState.value = _uiState.value.copy(isExpired = true, remainingSeconds = 0)
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

/** Formats seconds as MM:SS */
fun Int.toMinutesSeconds(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "%02d:%02d".format(minutes, seconds)
}
