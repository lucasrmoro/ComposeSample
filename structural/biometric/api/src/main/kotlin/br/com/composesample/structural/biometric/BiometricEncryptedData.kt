package br.com.composesample.structural.biometric

/** Opaque value ready to persist as a string and pass back to authenticate. */
data class BiometricEncryptedData(
    val value: String
)
