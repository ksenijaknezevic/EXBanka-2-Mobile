package rs.raf.exbanka.mobile.ui.screens.creditdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.CreditDetail
import rs.raf.exbanka.mobile.domain.repository.CreditRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

sealed class CreditDetailUiState {
    data object Loading : CreditDetailUiState()
    data class Success(val detail: CreditDetail) : CreditDetailUiState()
    data class Error(val message: String) : CreditDetailUiState()
}

@HiltViewModel
class CreditDetailViewModel @Inject constructor(
    private val creditRepository: CreditRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreditDetailUiState>(CreditDetailUiState.Loading)
    val uiState: StateFlow<CreditDetailUiState> = _uiState.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _uiState.value = CreditDetailUiState.Loading
            when (val result = creditRepository.getCreditDetails(id)) {
                is NetworkResult.Success -> _uiState.value = CreditDetailUiState.Success(result.data)
                is NetworkResult.Error -> _uiState.value = CreditDetailUiState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
