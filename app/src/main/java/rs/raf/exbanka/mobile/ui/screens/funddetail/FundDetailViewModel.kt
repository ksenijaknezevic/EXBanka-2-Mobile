package rs.raf.exbanka.mobile.ui.screens.funddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.FundPerformancePoint
import rs.raf.exbanka.mobile.domain.repository.FundRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

enum class PerformancePeriod(val value: String, val label: String) {
    Monthly("monthly", "Mesečno"),
    Quarterly("quarterly", "Kvartalno"),
    Yearly("yearly", "Godišnje")
}

data class FundDetailUiState(
    val isLoading: Boolean = true,
    val period: PerformancePeriod = PerformancePeriod.Monthly,
    val points: List<FundPerformancePoint> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FundDetailViewModel @Inject constructor(
    private val fundRepository: FundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FundDetailUiState())
    val uiState: StateFlow<FundDetailUiState> = _uiState.asStateFlow()

    private var currentFundId: String? = null

    fun load(fundId: String) {
        currentFundId = fundId
        fetch()
    }

    fun setPeriod(period: PerformancePeriod) {
        _uiState.update { it.copy(period = period) }
        fetch()
    }

    private fun fetch() {
        val id = currentFundId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = fundRepository.getPerformance(id, _uiState.value.period.value)) {
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
