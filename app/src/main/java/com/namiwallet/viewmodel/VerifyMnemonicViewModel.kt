package com.namiwallet.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VerifyMnemonicViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyMnemonicUiState())
    val uiState: StateFlow<VerifyMnemonicUiState> = _uiState.asStateFlow()

    fun initializeMnemonic(mnemonic: String) {
        val words = mnemonic.split(" ")
        // Select 3-4 random indices to verify
        val indices = words.indices.shuffled().take(4).sorted()

        _uiState.value = VerifyMnemonicUiState(
            originalWords = words,
            verificationIndices = indices,
            selectedWords = List(indices.size) { "" },
            shuffledOptions = words.shuffled()
        )
    }

    fun selectWord(verificationIndex: Int, word: String) {
        val currentSelected = _uiState.value.selectedWords.toMutableList()
        currentSelected[verificationIndex] = word
        _uiState.value = _uiState.value.copy(selectedWords = currentSelected)
    }

    fun clearWord(verificationIndex: Int) {
        val currentSelected = _uiState.value.selectedWords.toMutableList()
        currentSelected[verificationIndex] = ""
        _uiState.value = _uiState.value.copy(selectedWords = currentSelected)
    }

    fun verify(): Boolean {
        val state = _uiState.value
        val isCorrect = state.verificationIndices.mapIndexed { idx, originalIndex ->
            state.selectedWords[idx] == state.originalWords[originalIndex]
        }.all { it }

        _uiState.value = state.copy(
            isVerified = isCorrect,
            error = if (!isCorrect) "Incorrect words selected. Please try again." else null
        )
        return isCorrect
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class VerifyMnemonicUiState(
    val originalWords: List<String> = emptyList(),
    val verificationIndices: List<Int> = emptyList(),
    val selectedWords: List<String> = emptyList(),
    val shuffledOptions: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val error: String? = null
)
