package rs.raf.exbanka.mobile.ui.screens.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.Transaction
import rs.raf.exbanka.mobile.domain.model.VerificationCode
import rs.raf.exbanka.mobile.domain.repository.TransactionRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

data class TransactionDetailUiState(
    val isLoadingTransaction: Boolean = true,
    val transaction: Transaction? = null,
    val transactionError: String? = null,
    val isApproving: Boolean = false,
    val approveError: String? = null,
    val verificationCode: VerificationCode? = null
)

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    fun loadTransaction(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTransaction = true, transactionError = null)
            when (val result = transactionRepository.getTransactionById(id)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingTransaction = false,
                        transaction = result.data
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingTransaction = false,
                        transactionError = result.message
                    )
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun approveTransaction(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isApproving = true, approveError = null)
            when (val result = transactionRepository.approveTransaction(id)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isApproving = false,
                        verificationCode = result.data
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isApproving = false,
                        approveError = result.message
                    )
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearApproveError() {
        _uiState.value = _uiState.value.copy(approveError = null)
    }
}
