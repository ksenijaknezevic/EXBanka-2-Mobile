package rs.raf.exbanka.mobile.ui.screens.currencyexchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.Account
import rs.raf.exbanka.mobile.domain.model.ExchangeConversion
import rs.raf.exbanka.mobile.domain.model.ExchangeExecution
import rs.raf.exbanka.mobile.domain.repository.AccountRepository
import rs.raf.exbanka.mobile.domain.repository.ExchangeRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

data class CurrencyExchangeUiState(
    val isLoadingAccounts: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val sourceAccount: Account? = null,
    val targetAccount: Account? = null,
    val amountInput: String = "",
    val conversion: ExchangeConversion? = null,
    val isCalculating: Boolean = false,
    val previewError: String? = null,
    val isExecuting: Boolean = false,
    val executionResult: ExchangeExecution? = null,
    val executionError: String? = null,
    val loadError: String? = null
)

@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyExchangeUiState())
    val uiState: StateFlow<CurrencyExchangeUiState> = _uiState.asStateFlow()

    private var previewJob: Job? = null

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAccounts = true, loadError = null) }
            when (val result = accountRepository.getMyAccounts()) {
                is NetworkResult.Success -> {
                    val accounts = result.data
                    _uiState.update {
                        it.copy(
                            isLoadingAccounts = false,
                            accounts = accounts,
                            sourceAccount = accounts.firstOrNull(),
                            targetAccount = accounts.firstOrNull { acc ->
                                acc.id != accounts.firstOrNull()?.id && acc.valuta != accounts.firstOrNull()?.valuta
                            } ?: accounts.drop(1).firstOrNull()
                        )
                    }
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoadingAccounts = false, loadError = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun onSourceSelected(account: Account) {
        _uiState.update { it.copy(sourceAccount = account, conversion = null, previewError = null) }
        recalculatePreview()
    }

    fun onTargetSelected(account: Account) {
        _uiState.update { it.copy(targetAccount = account, conversion = null, previewError = null) }
        recalculatePreview()
    }

    fun onAmountChange(value: String) {
        val sanitized = value.replace(',', '.').filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(amountInput = sanitized, conversion = null, previewError = null) }
        recalculatePreview()
    }

    private fun recalculatePreview() {
        previewJob?.cancel()
        val state = _uiState.value
        val source = state.sourceAccount
        val target = state.targetAccount
        val amount = state.amountInput.toDoubleOrNull()

        if (source == null || target == null || amount == null || amount <= 0.0) return
        if (source.id == target.id) return
        if (source.valuta == target.valuta) return

        previewJob = viewModelScope.launch {
            delay(350) // debounce
            _uiState.update { it.copy(isCalculating = true, previewError = null) }
            when (val result = exchangeRepository.convert(source.valuta, target.valuta, amount)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isCalculating = false, conversion = result.data)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isCalculating = false, conversion = null, previewError = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun execute() {
        val state = _uiState.value
        val source = state.sourceAccount ?: return
        val target = state.targetAccount ?: return
        val amount = state.amountInput.toDoubleOrNull() ?: return

        if (source.id == target.id) {
            _uiState.update { it.copy(executionError = "Izvorni i ciljni račun moraju biti različiti") }
            return
        }
        if (source.valuta == target.valuta) {
            _uiState.update { it.copy(executionError = "Račun mora biti u drugoj valuti za konverziju") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isExecuting = true, executionError = null) }
            val sourceId = source.id.toLongOrNull()
            val targetId = target.id.toLongOrNull()
            if (sourceId == null || targetId == null) {
                _uiState.update {
                    it.copy(isExecuting = false, executionError = "Neispravan ID računa")
                }
                return@launch
            }
            when (val result = exchangeRepository.execute(
                sourceAccountId = sourceId,
                targetAccountId = targetId,
                fromOznaka = source.valuta,
                toOznaka = target.valuta,
                amount = amount
            )) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isExecuting = false, executionResult = result.data)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isExecuting = false, executionError = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearExecutionError() {
        _uiState.update { it.copy(executionError = null) }
    }

    fun acknowledgeResult() {
        _uiState.update {
            it.copy(
                executionResult = null,
                amountInput = "",
                conversion = null
            )
        }
    }
}
