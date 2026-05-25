package rs.raf.exbanka.mobile.ui.screens.exchangeratehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.ExchangeRateHistoryPoint
import rs.raf.exbanka.mobile.domain.repository.ExchangeRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

data class ExchangeRateHistoryUiState(
    val isLoading: Boolean = true,
    val selectedOznaka: String = "EUR",
    val availableOznake: List<String> = listOf("EUR", "USD", "CHF", "GBP", "JPY", "CAD", "AUD"),
    val points: List<ExchangeRateHistoryPoint> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ExchangeRateHistoryViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeRateHistoryUiState())
    val uiState: StateFlow<ExchangeRateHistoryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun selectCurrency(oznaka: String) {
        _uiState.update { it.copy(selectedOznaka = oznaka) }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val oznaka = _uiState.value.selectedOznaka
            when (val result = exchangeRepository.getHistory(oznaka = oznaka, days = 30)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, points = result.data)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
