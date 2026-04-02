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
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class SendViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SendUiState())
    val uiState: StateFlow<SendUiState> = _uiState.asStateFlow()

    init {
        loadBalance()
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
        loadBalance()
    }

    fun selectChain(chain: SupportedChain) {
        _uiState.value = _uiState.value.copy(selectedChain = chain)
        loadBalance()
    }

    private fun loadBalance() {
        viewModelScope.launch {
            walletRepository.getBalance(_uiState.value.selectedChain).fold(
                onSuccess = { balance ->
                    _uiState.value = _uiState.value.copy(availableBalance = balance)
                },
                onFailure = { /* Handle silently */ }
            )
        }
    }

    fun updateRecipient(address: String) {
        val isValid = walletRepository.validateAddress(address, _uiState.value.selectedChain)
        _uiState.value = _uiState.value.copy(
            recipientAddress = address,
            isAddressValid = if (address.isNotEmpty()) isValid else null
        )
    }

    fun updateAmount(amount: String) {
        // Validate amount format
        val isValid = try {
            if (amount.isEmpty()) null
            else {
                val decimal = BigDecimal(amount)
                decimal > BigDecimal.ZERO
            }
        } catch (e: Exception) {
            false
        }

        _uiState.value = _uiState.value.copy(
            amount = amount,
            isAmountValid = isValid
        )
    }

    fun setMaxAmount() {
        _uiState.value = _uiState.value.copy(
            amount = _uiState.value.availableBalance
        )
        updateAmount(_uiState.value.availableBalance)
    }

    fun sendTransaction(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validate inputs
        if (state.isAddressValid != true || state.isAmountValid != true) {
            _uiState.value = state.copy(error = "Please enter valid address and amount")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            val result = when (state.selectedChain) {
                SupportedChain.ETHEREUM, SupportedChain.BSC -> {
                    walletRepository.sendEvmTransaction(
                        chain = state.selectedChain,
                        to = state.recipientAddress,
                        amount = state.amount
                    )
                }
                SupportedChain.BITCOIN -> {
                    // Convert BTC to satoshi
                    val satoshi = (BigDecimal(state.amount) * BigDecimal("100000000")).toLong()
                    walletRepository.sendBitcoinTransaction(
                        to = state.recipientAddress,
                        amountSatoshi = satoshi
                    )
                }
            }

            result.fold(
                onSuccess = { txHash ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        transactionHash = txHash
                    )
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Transaction failed"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SendUiState(
    val selectedChain: SupportedChain = SupportedChain.ETHEREUM,
    val recipientAddress: String = "",
    val amount: String = "",
    val availableBalance: String = "0",
    val isAddressValid: Boolean? = null,
    val isAmountValid: Boolean? = null,
    val isLoading: Boolean = false,
    val transactionHash: String? = null,
    val error: String? = null
)
