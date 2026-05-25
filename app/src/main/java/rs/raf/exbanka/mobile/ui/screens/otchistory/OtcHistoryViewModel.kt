package rs.raf.exbanka.mobile.ui.screens.otchistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.OtcNegotiation
import rs.raf.exbanka.mobile.domain.repository.OtcRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

enum class OtcStatusFilter(val value: String?) {
    All(null),
    Accepted("ACCEPTED"),
    Rejected("REJECTED"),
    Deactivated("DEACTIVATED")
}

data class OtcHistoryFilters(
    val status: OtcStatusFilter = OtcStatusFilter.All,
    val from: String? = null,           // YYYY-MM-DD
    val to: String? = null,             // YYYY-MM-DD
    val counterpartIdInput: String = "" // free text; parsed to Long on submit
)

data class OtcHistoryUiState(
    val isLoading: Boolean = true,
    val negotiations: List<OtcNegotiation> = emptyList(),
    val error: String? = null,
    val filters: OtcHistoryFilters = OtcHistoryFilters()
)

@HiltViewModel
class OtcHistoryViewModel @Inject constructor(
    private val otcRepository: OtcRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtcHistoryUiState())
    val uiState: StateFlow<OtcHistoryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val f = _uiState.value.filters
            val counterpartId = f.counterpartIdInput.trim().toLongOrNull()
            when (val result = otcRepository.getHistory(
                status = f.status.value,
                from = f.from?.takeIf { it.isNotBlank() },
                to = f.to?.takeIf { it.isNotBlank() },
                counterpartId = counterpartId
            )) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isLoading = false, negotiations = result.data)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun onStatusChange(status: OtcStatusFilter) {
        _uiState.update { it.copy(filters = it.filters.copy(status = status)) }
        load()
    }

    fun onFromChange(value: String) {
        _uiState.update { it.copy(filters = it.filters.copy(from = value.ifBlank { null })) }
    }

    fun onToChange(value: String) {
        _uiState.update { it.copy(filters = it.filters.copy(to = value.ifBlank { null })) }
    }

    fun onCounterpartChange(value: String) {
        _uiState.update { it.copy(filters = it.filters.copy(counterpartIdInput = value)) }
    }

    fun applyFilters() {
        load()
    }

    fun clearFilters() {
        _uiState.update { it.copy(filters = OtcHistoryFilters()) }
        load()
    }
}
