package rs.raf.exbanka.mobile.ui.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.Account
import rs.raf.exbanka.mobile.domain.repository.AccountRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

sealed class AccountsUiState {
    data object Loading : AccountsUiState()
    data class Success(val accounts: List<Account>) : AccountsUiState()
    data object Empty : AccountsUiState()
    data class Error(val message: String) : AccountsUiState()
}

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountsUiState>(AccountsUiState.Loading)
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.value = AccountsUiState.Loading
            when (val result = accountRepository.getMyAccounts()) {
                is NetworkResult.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        AccountsUiState.Empty
                    } else {
                        AccountsUiState.Success(result.data)
                    }
                }
                is NetworkResult.Error -> _uiState.value = AccountsUiState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
