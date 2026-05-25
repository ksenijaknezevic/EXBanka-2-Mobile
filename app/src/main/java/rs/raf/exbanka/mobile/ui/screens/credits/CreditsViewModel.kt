package rs.raf.exbanka.mobile.ui.screens.credits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.Credit
import rs.raf.exbanka.mobile.domain.repository.CreditRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

sealed class CreditsUiState {
    data object Loading : CreditsUiState()
    data class Success(val credits: List<Credit>) : CreditsUiState()
    data object Empty : CreditsUiState()
    data class Error(val message: String) : CreditsUiState()
}

@HiltViewModel
class CreditsViewModel @Inject constructor(
    private val creditRepository: CreditRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreditsUiState>(CreditsUiState.Loading)
    val uiState: StateFlow<CreditsUiState> = _uiState.asStateFlow()

    init {
        loadCredits()
    }

    fun loadCredits() {
        viewModelScope.launch {
            _uiState.value = CreditsUiState.Loading
            when (val result = creditRepository.getClientCredits()) {
                is NetworkResult.Success -> {
                    val active = result.data.filter { it.status != "OTPLACEN" && it.status != "ODBIJEN" }
                    _uiState.value = if (active.isEmpty()) {
                        CreditsUiState.Empty
                    } else {
                        CreditsUiState.Success(active)
                    }
                }
                is NetworkResult.Error -> _uiState.value = CreditsUiState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
