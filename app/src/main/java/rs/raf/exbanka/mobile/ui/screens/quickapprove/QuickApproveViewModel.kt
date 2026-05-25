package rs.raf.exbanka.mobile.ui.screens.quickapprove

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.Transaction
import rs.raf.exbanka.mobile.domain.repository.TransactionRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import java.time.Instant
import javax.inject.Inject

enum class QuickApproveItemStatus {
    Pending,        // čeka odluku korisnika
    Processing,     // approve + verify u toku
    Approved,       // uspešno odobreno
    Expired,        // istekao prozor (5 min od kreiranja)
    Failed          // greška pri obradi
}

data class QuickApproveItem(
    val transaction: Transaction,
    val itemStatus: QuickApproveItemStatus = QuickApproveItemStatus.Pending,
    val errorMessage: String? = null,
    val remainingSeconds: Long = 0L
)

data class QuickApproveUiState(
    val isLoading: Boolean = true,
    val items: List<QuickApproveItem> = emptyList(),
    val loadError: String? = null
)

@HiltViewModel
class QuickApproveViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickApproveUiState())
    val uiState: StateFlow<QuickApproveUiState> = _uiState.asStateFlow()

    init {
        load()
        startTicker()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            when (val result = transactionRepository.getPendingTransactions()) {
                is NetworkResult.Success -> _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        items = result.data.map { tx ->
                            QuickApproveItem(
                                transaction = tx,
                                remainingSeconds = remainingFor(tx)
                            )
                        }
                    )
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, loadError = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun quickApprove(transactionId: String) {
        val current = _uiState.value.items.find { it.transaction.id == transactionId } ?: return
        if (current.itemStatus != QuickApproveItemStatus.Pending) return
        if (current.remainingSeconds <= 0L) {
            updateItem(transactionId) {
                it.copy(itemStatus = QuickApproveItemStatus.Expired)
            }
            return
        }

        viewModelScope.launch {
            updateItem(transactionId) {
                it.copy(itemStatus = QuickApproveItemStatus.Processing, errorMessage = null)
            }

            val approveResult = transactionRepository.approveTransaction(transactionId)
            if (approveResult is NetworkResult.Error) {
                updateItem(transactionId) {
                    it.copy(itemStatus = QuickApproveItemStatus.Failed, errorMessage = approveResult.message)
                }
                return@launch
            }
            val code = (approveResult as NetworkResult.Success).data.code

            val verifyResult = transactionRepository.verifyAction(transactionId, code)
            when (verifyResult) {
                is NetworkResult.Success -> updateItem(transactionId) {
                    it.copy(itemStatus = QuickApproveItemStatus.Approved)
                }
                is NetworkResult.Error -> updateItem(transactionId) {
                    it.copy(itemStatus = QuickApproveItemStatus.Failed, errorMessage = verifyResult.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun dismiss(transactionId: String) {
        _uiState.update { state ->
            state.copy(items = state.items.filter { it.transaction.id != transactionId })
        }
    }

    private fun startTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    state.copy(
                        items = state.items.map { item ->
                            if (item.itemStatus == QuickApproveItemStatus.Pending ||
                                item.itemStatus == QuickApproveItemStatus.Expired
                            ) {
                                val remaining = remainingFor(item.transaction)
                                val newStatus = if (remaining <= 0L && item.itemStatus == QuickApproveItemStatus.Pending) {
                                    QuickApproveItemStatus.Expired
                                } else item.itemStatus
                                item.copy(remainingSeconds = remaining.coerceAtLeast(0L), itemStatus = newStatus)
                            } else item
                        }
                    )
                }
            }
        }
    }

    private fun remainingFor(tx: Transaction): Long {
        val expiresAt = tx.createdAt.plusSeconds(EXPIRY_SECONDS)
        return java.time.Duration.between(Instant.now(), expiresAt).seconds
    }

    private inline fun updateItem(id: String, transform: (QuickApproveItem) -> QuickApproveItem) {
        _uiState.update { state ->
            state.copy(items = state.items.map { if (it.transaction.id == id) transform(it) else it })
        }
    }

    companion object {
        private const val EXPIRY_SECONDS = 5L * 60L
    }
}
