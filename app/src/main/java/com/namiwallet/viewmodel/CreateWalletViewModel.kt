package com.namiwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namiwallet.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateWalletUiState())
    val uiState: StateFlow<CreateWalletUiState> = _uiState.asStateFlow()

    fun generateMnemonic(use24Words: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            walletRepository.createWallet(use24Words).fold(
                onSuccess = { mnemonic ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mnemonic = mnemonic,
                        mnemonicWords = mnemonic.split(" ")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to generate mnemonic"
                    )
                }
            )
        }
    }

    fun toggleWordCount() {
        val currentUse24 = _uiState.value.use24Words
        _uiState.value = _uiState.value.copy(use24Words = !currentUse24)
        generateMnemonic(!currentUse24)
    }

    fun acknowledgeBackup() {
        _uiState.value = _uiState.value.copy(backupAcknowledged = true)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CreateWalletUiState(
    val isLoading: Boolean = false,
    val mnemonic: String = "",
    val mnemonicWords: List<String> = emptyList(),
    val use24Words: Boolean = false,
    val backupAcknowledged: Boolean = false,
    val error: String? = null
)
