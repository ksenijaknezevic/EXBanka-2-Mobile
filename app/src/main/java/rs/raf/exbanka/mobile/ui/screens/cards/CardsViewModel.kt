package rs.raf.exbanka.mobile.ui.screens.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.raf.exbanka.mobile.domain.model.Card
import rs.raf.exbanka.mobile.domain.repository.CardRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

sealed class CardsUiState {
    data object Loading : CardsUiState()
    data class Success(val cards: List<Card>) : CardsUiState()
    data object Empty : CardsUiState()
    data class Error(val message: String) : CardsUiState()
}

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CardsUiState>(CardsUiState.Loading)
    val uiState: StateFlow<CardsUiState> = _uiState.asStateFlow()

    /** One-shot poruka koja se prikazuje kao Snackbar nakon (de)akcija. */
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    /** ID kartice koja se trenutno blokira (radi disable-a dugmeta). */
    private val _blockingId = MutableStateFlow<String?>(null)
    val blockingId: StateFlow<String?> = _blockingId.asStateFlow()

    init {
        loadCards()
    }

    fun loadCards() {
        viewModelScope.launch {
            _uiState.value = CardsUiState.Loading
            when (val result = cardRepository.getMyCards()) {
                is NetworkResult.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        CardsUiState.Empty
                    } else {
                        CardsUiState.Success(result.data)
                    }
                }
                is NetworkResult.Error -> _uiState.value = CardsUiState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun blockCard(id: String) {
        viewModelScope.launch {
            _blockingId.value = id
            when (val result = cardRepository.blockCard(id)) {
                is NetworkResult.Success -> {
                    _message.value = "Kartica je uspešno blokirana"
                    loadCards()
                }
                is NetworkResult.Error -> _message.value = result.message
                is NetworkResult.Loading -> Unit
            }
            _blockingId.value = null
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}
