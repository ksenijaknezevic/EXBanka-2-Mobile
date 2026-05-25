package rs.raf.exbanka.mobile.ui.screens.exchangerates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.ExchangeRate
import rs.raf.exbanka.mobile.domain.repository.ExchangeRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

sealed class ExchangeRatesUiState {
    data object Loading : ExchangeRatesUiState()
    data class Success(val rates: List<ExchangeRate>) : ExchangeRatesUiState()
    data object Empty : ExchangeRatesUiState()
    data class Error(val message: String) : ExchangeRatesUiState()
}

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExchangeRatesUiState>(ExchangeRatesUiState.Loading)
    val uiState: StateFlow<ExchangeRatesUiState> = _uiState.asStateFlow()

    init {
        loadRates()
    }

    fun loadRates() {
        viewModelScope.launch {
            _uiState.value = ExchangeRatesUiState.Loading
            when (val result = exchangeRepository.getRates()) {
                is NetworkResult.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        ExchangeRatesUiState.Empty
                    } else {
                        ExchangeRatesUiState.Success(result.data)
                    }
                }
                is NetworkResult.Error -> _uiState.value = ExchangeRatesUiState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
