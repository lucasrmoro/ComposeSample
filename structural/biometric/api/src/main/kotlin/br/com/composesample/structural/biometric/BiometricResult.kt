package br.com.composesample.structural.biometric

/** The successful value or classified failure of a biometric cryptographic operation. */
sealed interface BiometricResult<out T> {

    data class Success<T>(val data: T) : BiometricResult<T>

    data class Failure(val error: BiometricError) : BiometricResult<Nothing>

}
