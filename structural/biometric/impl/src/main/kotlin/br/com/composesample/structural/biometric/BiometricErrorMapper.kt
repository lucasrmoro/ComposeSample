package br.com.composesample.structural.biometric

import androidx.biometric.BiometricPrompt

internal fun Int.toBiometricError(): BiometricError = when (this) {
    BiometricPrompt.ERROR_CANCELED,
    BiometricPrompt.ERROR_USER_CANCELED,
    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricError.CANCELLED

    BiometricPrompt.ERROR_LOCKOUT,
    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricError.LOCKED_OUT

    BiometricPrompt.ERROR_NO_BIOMETRICS -> BiometricError.NOT_ENROLLED

    BiometricPrompt.ERROR_HW_NOT_PRESENT,
    BiometricPrompt.ERROR_HW_UNAVAILABLE,
    BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED -> BiometricError.UNAVAILABLE

    else -> BiometricError.AUTHENTICATION_FAILED
}
