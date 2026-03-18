package rs.raf.exbanka.mobile.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.Transaction
import rs.raf.exbanka.mobile.domain.repository.AuthRepository
import rs.raf.exbanka.mobile.domain.repository.TransactionRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

sealed class PendingTransactionsUiState {
    data object Loading : PendingTransactionsUiState()
    data class Success(val transactions: List<Transaction>) : PendingTransactionsUiState()
    data object Empty : PendingTransactionsUiState()
    data class Error(val message: String) : PendingTransactionsUiState()
}

@HiltViewModel
class PendingTransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PendingTransactionsUiState>(
        PendingTransactionsUiState.Loading
    )
    val uiState: StateFlow<PendingTransactionsUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = PendingTransactionsUiState.Loading
            when (val result = transactionRepository.getPendingTransactions()) {
                is NetworkResult.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        PendingTransactionsUiState.Empty
                    } else {
                        PendingTransactionsUiState.Success(result.data)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = PendingTransactionsUiState.Error(result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
