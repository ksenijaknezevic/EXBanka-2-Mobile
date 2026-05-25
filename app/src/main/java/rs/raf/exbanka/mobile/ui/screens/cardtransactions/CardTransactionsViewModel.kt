package rs.raf.exbanka.mobile.ui.screens.cardtransactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.AccountTransaction
import rs.raf.exbanka.mobile.domain.repository.AccountRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

sealed class CardTransactionsUiState {
    data object Loading : CardTransactionsUiState()
    data class Success(val cardLabel: String, val transactions: List<AccountTransaction>) :
        CardTransactionsUiState()
    data class Empty(val cardLabel: String) : CardTransactionsUiState()
    data class Error(val message: String) : CardTransactionsUiState()
}

/**
 * ViewModel za prikaz istorije transakcija konkretne kartice.
 * Pošto backend nema /bank/cards/{id}/transactions, koristimo
 * /bank/client/accounts/{racunId}/transactions za račun na koji je
 * kartica vezana — kartica je samo "ulaz" u taj račun. Valutu izvlačimo
 * iz GET /bank/client/accounts radi konzistentnog prikaza iznosa.
 */
@HiltViewModel
class CardTransactionsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val racunId: String = checkNotNull(savedStateHandle["racunId"])
    private val cardLabel: String = savedStateHandle["cardLabel"] ?: "Kartica"

    private val _uiState = MutableStateFlow<CardTransactionsUiState>(CardTransactionsUiState.Loading)
    val uiState: StateFlow<CardTransactionsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = CardTransactionsUiState.Loading

            // 1) Nađi valutu računa (radi formatiranja iznosa).
            val currency = when (val res = accountRepository.getMyAccounts()) {
                is NetworkResult.Success ->
                    res.data.firstOrNull { it.id == racunId }?.valuta ?: ""
                else -> ""
            }

            // 2) Dohvati transakcije za račun.
            when (val res = accountRepository.getAccountTransactions(racunId, currency)) {
                is NetworkResult.Success -> {
                    _uiState.value = if (res.data.isEmpty()) {
                        CardTransactionsUiState.Empty(cardLabel)
                    } else {
                        CardTransactionsUiState.Success(cardLabel, res.data)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = CardTransactionsUiState.Error(res.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
