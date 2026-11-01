package br.com.composesample.structural.biometric

import android.security.keystore.KeyPermanentlyInvalidatedException
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.ProviderException

internal fun <T> encryptionResult(operation: () -> T): BiometricResult<T> =
    cryptographyResult(operation).fold(
        onSuccess = { BiometricResult.Success(it) },
        onFailure = { BiometricResult.Failure(it.keyError() ?: BiometricError.ENCRYPTION_FAILED) }
    )

internal fun <T> decryptionResult(operation: () -> T): BiometricResult<T> =
    cryptographyResult(operation).fold(
        onSuccess = { BiometricResult.Success(it) },
        onFailure = { BiometricResult.Failure(it.keyError() ?: BiometricError.DECRYPTION_FAILED) }
    )

private fun <T> cryptographyResult(operation: () -> T): Result<T> = try {
    Result.success(operation())
} catch (exception: GeneralSecurityException) {
    Result.failure(exception)
} catch (exception: IllegalArgumentException) {
    Result.failure(exception)
} catch (exception: IOException) {
    Result.failure(exception)
} catch (exception: ProviderException) {
    Result.failure(exception)
}

private fun Throwable.keyError(): BiometricError? = when (this) {
    is KeyPermanentlyInvalidatedException -> BiometricError.BIOMETRIC_REACTIVATION_REQUIRED
    is BiometricKeyNotFoundException -> BiometricError.KEY_NOT_FOUND
    else -> null
}

internal class BiometricKeyNotFoundException : GeneralSecurityException()
