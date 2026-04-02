package com.namiwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namiwallet.bridge.NamiCore
import com.namiwallet.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val namiCore: NamiCore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportWalletUiState())
    val uiState: StateFlow<ImportWalletUiState> = _uiState.asStateFlow()

    fun updateWord(index: Int, word: String) {
        val currentWords = _uiState.value.words.toMutableList()
        if (index < currentWords.size) {
            currentWords[index] = word.lowercase().trim()
        }
        _uiState.value = _uiState.value.copy(
            words = currentWords,
            isValid = null,
            error = null
        )
    }

    fun updateMnemonicText(text: String) {
        val words = text.trim()
            .split(Regex("\\s+"))
            .map { it.lowercase().trim() }
            .filter { it.isNotEmpty() }

        val paddedWords = if (words.size <= 12) {
            words + List(12 - words.size) { "" }
        } else {
            words + List(24 - words.size.coerceAtMost(24)) { "" }
        }

        _uiState.value = _uiState.value.copy(
            words = paddedWords.take(24),
            mnemonicText = text,
            isValid = null,
            error = null
        )
    }

    fun updatePassphrase(passphrase: String) {
        _uiState.value = _uiState.value.copy(passphrase = passphrase)
    }

    fun getWordSuggestions(prefix: String): List<String> {
        if (prefix.length < 2) return emptyList()
        return namiCore.getWordSuggestions(prefix)
    }

    fun validateMnemonic(): Boolean {
        val words = _uiState.value.words.filter { it.isNotEmpty() }
        val mnemonic = words.joinToString(" ")

        val isValid = namiCore.validateMnemonic(mnemonic)
        _uiState.value = _uiState.value.copy(
            isValid = isValid,
            error = if (!isValid) "Invalid recovery phrase" else null
        )
        return isValid
    }

    fun importWallet(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (!validateMnemonic()) return@launch

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val words = _uiState.value.words.filter { it.isNotEmpty() }
            val mnemonic = words.joinToString(" ")

            walletRepository.importWallet(mnemonic, _uiState.value.passphrase).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to import wallet"
                    )
                }
            )
        }
    }

    fun toggleInputMode() {
        _uiState.value = _uiState.value.copy(
            useTextInput = !_uiState.value.useTextInput
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ImportWalletUiState(
    val words: List<String> = List(12) { "" },
    val mnemonicText: String = "",
    val passphrase: String = "",
    val isLoading: Boolean = false,
    val isValid: Boolean? = null,
    val useTextInput: Boolean = false,
    val error: String? = null
)
