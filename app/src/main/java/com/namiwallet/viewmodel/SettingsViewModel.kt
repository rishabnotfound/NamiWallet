package com.namiwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namiwallet.repository.WalletRepository
import com.namiwallet.security.BiometricAvailability
import com.namiwallet.security.BiometricHelper
import com.namiwallet.security.SecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val secureStorage: SecureStorage,
    private val biometricHelper: BiometricHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.value = _uiState.value.copy(
            biometricEnabled = secureStorage.isBiometricEnabled(),
            selectedCurrency = secureStorage.getSelectedCurrency(),
            selectedTheme = secureStorage.getTheme(),
            biometricAvailable = biometricHelper.isBiometricAvailable() == BiometricAvailability.Available
        )
    }

    fun toggleBiometric(enabled: Boolean) {
        secureStorage.setBiometricEnabled(enabled)
        _uiState.value = _uiState.value.copy(biometricEnabled = enabled)
    }

    fun setTheme(theme: String) {
        secureStorage.setTheme(theme)
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
    }

    fun setCurrency(currency: String) {
        secureStorage.setSelectedCurrency(currency)
        _uiState.value = _uiState.value.copy(selectedCurrency = currency)
    }

    fun showMnemonic(onMnemonicReady: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            walletRepository.getMnemonicForBackup().fold(
                onSuccess = { mnemonic ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mnemonic = mnemonic
                    )
                    onMnemonicReady(mnemonic)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to retrieve mnemonic"
                    )
                }
            )
        }
    }

    fun hideMnemonic() {
        _uiState.value = _uiState.value.copy(mnemonic = null)
    }

    fun deleteWallet(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            walletRepository.deleteWallet().fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onDeleted()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to delete wallet"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SettingsUiState(
    val biometricEnabled: Boolean = false,
    val biometricAvailable: Boolean = false,
    val selectedCurrency: String = "USD",
    val selectedTheme: String = "system",
    val mnemonic: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
