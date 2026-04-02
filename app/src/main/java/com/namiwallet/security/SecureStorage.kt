package com.namiwallet.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage using Android EncryptedSharedPreferences.
 * All sensitive data is encrypted at rest using Android Keystore.
 */
@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // ========================================================================
    // Mnemonic Storage
    // ========================================================================

    /**
     * Securely store the wallet mnemonic
     */
    suspend fun storeMnemonic(mnemonic: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit()
                .putString(KEY_MNEMONIC, mnemonic)
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(SecureStorageException("Failed to store mnemonic: ${e.message}"))
        }
    }

    /**
     * Retrieve the stored mnemonic
     */
    suspend fun getMnemonic(): Result<String?> = withContext(Dispatchers.IO) {
        try {
            val mnemonic = encryptedPrefs.getString(KEY_MNEMONIC, null)
            Result.success(mnemonic)
        } catch (e: Exception) {
            Result.failure(SecureStorageException("Failed to retrieve mnemonic: ${e.message}"))
        }
    }

    /**
     * Check if a mnemonic is stored
     */
    fun hasMnemonic(): Boolean {
        return encryptedPrefs.contains(KEY_MNEMONIC)
    }

    /**
     * Delete the stored mnemonic
     */
    suspend fun deleteMnemonic(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit()
                .remove(KEY_MNEMONIC)
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(SecureStorageException("Failed to delete mnemonic: ${e.message}"))
        }
    }

    // ========================================================================
    // Passphrase Storage (Optional BIP-39 passphrase)
    // ========================================================================

    /**
     * Store the BIP-39 passphrase (optional additional security)
     */
    suspend fun storePassphrase(passphrase: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit()
                .putString(KEY_PASSPHRASE, passphrase)
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(SecureStorageException("Failed to store passphrase: ${e.message}"))
        }
    }

    /**
     * Retrieve the stored passphrase
     */
    suspend fun getPassphrase(): String {
        return encryptedPrefs.getString(KEY_PASSPHRASE, "") ?: ""
    }

    // ========================================================================
    // Account Index Storage
    // ========================================================================

    /**
     * Store the current account index for a chain
     */
    fun setAccountIndex(chain: String, index: Int) {
        encryptedPrefs.edit()
            .putInt("${KEY_ACCOUNT_INDEX_PREFIX}$chain", index)
            .apply()
    }

    /**
     * Get the current account index for a chain
     */
    fun getAccountIndex(chain: String): Int {
        return encryptedPrefs.getInt("${KEY_ACCOUNT_INDEX_PREFIX}$chain", 0)
    }

    // ========================================================================
    // Settings Storage
    // ========================================================================

    /**
     * Store biometric authentication preference
     */
    fun setBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    /**
     * Check if biometric authentication is enabled
     */
    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    /**
     * Store the selected currency for display
     */
    fun setSelectedCurrency(currency: String) {
        encryptedPrefs.edit()
            .putString(KEY_SELECTED_CURRENCY, currency)
            .apply()
    }

    /**
     * Get the selected currency
     */
    fun getSelectedCurrency(): String {
        return encryptedPrefs.getString(KEY_SELECTED_CURRENCY, "USD") ?: "USD"
    }

    /**
     * Store the selected theme
     */
    fun setTheme(theme: String) {
        encryptedPrefs.edit()
            .putString(KEY_THEME, theme)
            .apply()
    }

    /**
     * Get the selected theme
     */
    fun getTheme(): String {
        return encryptedPrefs.getString(KEY_THEME, "system") ?: "system"
    }

    // ========================================================================
    // Wallet State
    // ========================================================================

    /**
     * Mark wallet setup as complete
     */
    fun setWalletSetupComplete(complete: Boolean) {
        encryptedPrefs.edit()
            .putBoolean(KEY_WALLET_SETUP_COMPLETE, complete)
            .apply()
    }

    /**
     * Check if wallet setup is complete
     */
    fun isWalletSetupComplete(): Boolean {
        return encryptedPrefs.getBoolean(KEY_WALLET_SETUP_COMPLETE, false)
    }

    /**
     * Store last backup timestamp
     */
    fun setLastBackupTime(timestamp: Long) {
        encryptedPrefs.edit()
            .putLong(KEY_LAST_BACKUP_TIME, timestamp)
            .apply()
    }

    /**
     * Get last backup timestamp
     */
    fun getLastBackupTime(): Long {
        return encryptedPrefs.getLong(KEY_LAST_BACKUP_TIME, 0L)
    }

    // ========================================================================
    // Clear All Data
    // ========================================================================

    /**
     * Clear all stored data (used when resetting wallet)
     */
    suspend fun clearAll(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit().clear().apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(SecureStorageException("Failed to clear storage: ${e.message}"))
        }
    }

    companion object {
        private const val ENCRYPTED_PREFS_FILE = "nami_wallet_secure_prefs"

        private const val KEY_MNEMONIC = "mnemonic"
        private const val KEY_PASSPHRASE = "passphrase"
        private const val KEY_ACCOUNT_INDEX_PREFIX = "account_index_"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_SELECTED_CURRENCY = "selected_currency"
        private const val KEY_THEME = "theme"
        private const val KEY_WALLET_SETUP_COMPLETE = "wallet_setup_complete"
        private const val KEY_LAST_BACKUP_TIME = "last_backup_time"
    }
}

class SecureStorageException(message: String) : Exception(message)
