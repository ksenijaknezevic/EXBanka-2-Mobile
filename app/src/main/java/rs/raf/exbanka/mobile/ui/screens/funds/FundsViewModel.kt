package rs.raf.exbanka.mobile.ui.screens.funds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.FundsOverview
import rs.raf.exbanka.mobile.domain.repository.FundRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

enum class FundsSort {
    Name,
    FundValue,
    Profit
}

sealed class FundsUiState {
    data object Loading : FundsUiState()
    data class Success(val overview: FundsOverview, val sort: FundsSort) : FundsUiState()
    data object Empty : FundsUiState()
    data class Error(val message: String) : FundsUiState()
}

@HiltViewModel
class FundsViewModel @Inject constructor(
    private val fundRepository: FundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FundsUiState>(FundsUiState.Loading)
    val uiState: StateFlow<FundsUiState> = _uiState.asStateFlow()

    private var currentSort = FundsSort.Name

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = FundsUiState.Loading
            when (val result = fundRepository.getFunds()) {
                is NetworkResult.Success -> {
                    val o = result.data
                    if (o.clientFunds.isEmpty() && o.managedFunds.isEmpty()) {
                        _uiState.value = FundsUiState.Empty
                    } else {
                        _uiState.value = FundsUiState.Success(applySort(o, currentSort), currentSort)
                    }
                }
                is NetworkResult.Error -> _uiState.value = FundsUiState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun setSort(sort: FundsSort) {
        currentSort = sort
        val state = _uiState.value
        if (state is FundsUiState.Success) {
            _uiState.value = FundsUiState.Success(applySort(state.overview, sort), sort)
        }
    }

    private fun applySort(overview: FundsOverview, sort: FundsSort): FundsOverview {
        val client = when (sort) {
            FundsSort.Name -> overview.clientFunds.sortedBy { it.name.lowercase() }
            FundsSort.FundValue -> overview.clientFunds.sortedByDescending { it.fundValueRsd }
            FundsSort.Profit -> overview.clientFunds.sortedByDescending { it.profit }
        }
        val managed = when (sort) {
            FundsSort.Name -> overview.managedFunds.sortedBy { it.name.lowercase() }
            FundsSort.FundValue -> overview.managedFunds.sortedByDescending { it.fundValueRsd }
            FundsSort.Profit -> overview.managedFunds.sortedByDescending { it.liquidityRsd }
        }
        return overview.copy(clientFunds = client, managedFunds = managed)
    }
}
