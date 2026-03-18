package rs.raf.exbanka.mobile.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.repository.AuthRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val isLoggedIn = authRepository.isLoggedIn()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.value = state.copy(error = "Email is required")
            return
        }
        if (state.password.isBlank()) {
            _uiState.value = state.copy(error = "Password is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.login(state.email.trim(), state.password)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }
}
