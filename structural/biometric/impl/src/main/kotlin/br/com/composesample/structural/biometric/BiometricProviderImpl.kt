package br.com.composesample.structural.biometric

import javax.crypto.Cipher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class BiometricProviderImpl(
    private val authenticator: BiometricAuthenticator,
    private val keyStore: BiometricKeyStore,
    private val biometricCipher: BiometricCipher,
    private val mainDispatcher: CoroutineDispatcher
) : BiometricProvider {

    override fun getStatus(): BiometricStatus = authenticator.getStatus()

    override suspend fun enroll(
        prompt: BiometricPromptConfig
    ): BiometricResult<BiometricEncryption> = authorize(
        prompt = prompt,
        prepare = ::prepareEnrollmentCipher,
        authorized = { BiometricEncryptionImpl(it, biometricCipher) }
    )

    override suspend fun authenticate(
        data: BiometricEncryptedData,
        prompt: BiometricPromptConfig
    ): BiometricResult<BiometricDecryption> = authorize(
        prompt = prompt,
        prepare = {
            decryptionResult {
                val key = keyStore.get() ?: throw BiometricKeyNotFoundException()
                biometricCipher.forDecryption(key, data)
            }
        },
        authorized = { BiometricDecryptionImpl(it, biometricCipher, data) }
    )

    private fun prepareEnrollmentCipher(): BiometricResult<Cipher> = encryptionResult {
        biometricCipher.forEncryption(keyStore.getOrCreate())
    }

    private suspend fun <T> authorize(
        prompt: BiometricPromptConfig,
        prepare: () -> BiometricResult<Cipher>,
        authorized: (Cipher) -> T
    ): BiometricResult<T> = withContext(mainDispatcher) {
        check(authenticator.screenIsReady()) {
            "Biometric operations require a resumed screen"
        }
        val status = getStatus()
        val error = when {
            authenticator.isAuthenticating -> BiometricError.OPERATION_IN_PROGRESS
            status == BiometricStatus.NOT_ENROLLED -> BiometricError.NOT_ENROLLED
            status != BiometricStatus.AVAILABLE -> BiometricError.UNAVAILABLE
            else -> null
        }
        if (error != null) {
            return@withContext BiometricResult.Failure(error)
        }
        when (val prepared = prepare()) {
            is BiometricResult.Failure -> prepared
            is BiometricResult.Success -> when (
                val result = authenticator.authenticate(prepared.data, prompt)
            ) {
                is BiometricResult.Failure -> result
                is BiometricResult.Success -> BiometricResult.Success(authorized(result.data))
            }
        }
    }

}
