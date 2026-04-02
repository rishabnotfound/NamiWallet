package com.namiwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namiwallet.repository.SupportedChain
import com.namiwallet.repository.WalletAddresses
import com.namiwallet.repository.WalletBalances
import com.namiwallet.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    fun loadWalletData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load addresses
            walletRepository.getAllAddresses().fold(
                onSuccess = { addresses ->
                    _uiState.value = _uiState.value.copy(addresses = addresses)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to load addresses"
                    )
                }
            )

            // Load balances
            walletRepository.getAllBalances().collect { result ->
                result.fold(
                    onSuccess = { balances ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            balances = balances
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load balances"
                        )
                    }
                )
            }
        }
    }

    fun refresh() {
        loadWalletData()
    }

    fun selectChain(chain: SupportedChain) {
        _uiState.value = _uiState.value.copy(selectedChain = chain)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val addresses: WalletAddresses? = null,
    val balances: WalletBalances? = null,
    val selectedChain: SupportedChain = SupportedChain.ETHEREUM,
    val error: String? = null
)
