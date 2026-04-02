package com.namiwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namiwallet.repository.SupportedChain
import com.namiwallet.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiveViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiveUiState())
    val uiState: StateFlow<ReceiveUiState> = _uiState.asStateFlow()

    init {
        loadAddresses()
    }

    fun setInitialChain(chainName: String?) {
        val chain = chainName?.let {
            try {
                SupportedChain.valueOf(it)
            } catch (e: Exception) {
                SupportedChain.ETHEREUM
            }
        } ?: SupportedChain.ETHEREUM

        _uiState.value = _uiState.value.copy(selectedChain = chain)
        updateDisplayedAddress()
    }

    fun selectChain(chain: SupportedChain) {
        _uiState.value = _uiState.value.copy(selectedChain = chain)
        updateDisplayedAddress()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            walletRepository.getAllAddresses().fold(
                onSuccess = { addresses ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        ethereumAddress = addresses.ethereum,
                        bscAddress = addresses.bsc,
                        bitcoinAddress = addresses.bitcoin
                    )
                    updateDisplayedAddress()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load addresses"
                    )
                }
            )
        }
    }

    private fun updateDisplayedAddress() {
        val state = _uiState.value
        val address = when (state.selectedChain) {
            SupportedChain.ETHEREUM -> state.ethereumAddress
            SupportedChain.BSC -> state.bscAddress
            SupportedChain.BITCOIN -> state.bitcoinAddress
        }
        _uiState.value = state.copy(displayedAddress = address)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ReceiveUiState(
    val selectedChain: SupportedChain = SupportedChain.ETHEREUM,
    val ethereumAddress: String = "",
    val bscAddress: String = "",
    val bitcoinAddress: String = "",
    val displayedAddress: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
