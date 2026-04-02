package com.namiwallet.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for biometric authentication operations.
 */
@Singleton
class BiometricHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val biometricManager = BiometricManager.from(context)

    /**
     * Check if biometric authentication is available on the device
     */
    fun isBiometricAvailable(): BiometricAvailability {
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NoneEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SecurityUpdateRequired
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> BiometricAvailability.Unsupported
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> BiometricAvailability.Unknown
            else -> BiometricAvailability.Unknown
        }
    }

    /**
     * Prompt for biometric authentication
     */
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String = "Authenticate",
        subtitle: String = "Verify your identity to continue",
        negativeButtonText: String = "Cancel"
    ): BiometricResult {
        val resultChannel = Channel<BiometricResult>(Channel.CONFLATED)

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                resultChannel.trySend(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                val result = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricResult.Cancelled
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricResult.Lockout(errString.toString())
                    else -> BiometricResult.Error(errString.toString())
                }
                resultChannel.trySend(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Don't send result here - let the user try again
                // The system will call onAuthenticationError after too many failures
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        activity.runOnUiThread {
            biometricPrompt.authenticate(promptInfo)
        }

        return resultChannel.receiveAsFlow().first { true }
    }

    /**
     * Check if device has a screen lock (PIN, pattern, password)
     */
    fun hasScreenLock(): Boolean {
        return biometricManager.canAuthenticate(DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }
}

sealed class BiometricAvailability {
    object Available : BiometricAvailability()
    object NoHardware : BiometricAvailability()
    object HardwareUnavailable : BiometricAvailability()
    object NoneEnrolled : BiometricAvailability()
    object SecurityUpdateRequired : BiometricAvailability()
    object Unsupported : BiometricAvailability()
    object Unknown : BiometricAvailability()
}

sealed class BiometricResult {
    object Success : BiometricResult()
    object Cancelled : BiometricResult()
    data class Error(val message: String) : BiometricResult()
    data class Lockout(val message: String) : BiometricResult()
}

// Extension to await first emission
private suspend fun <T> kotlinx.coroutines.flow.Flow<T>.first(predicate: (T) -> Boolean): T {
    var result: T? = null
    collect { value ->
        if (predicate(value)) {
            result = value
            return@collect
        }
    }
    @Suppress("UNCHECKED_CAST")
    return result as T
}
