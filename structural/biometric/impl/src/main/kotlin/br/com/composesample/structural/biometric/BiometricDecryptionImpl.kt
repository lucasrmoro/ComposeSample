package br.com.composesample.structural.biometric

import java.util.concurrent.atomic.AtomicBoolean
import javax.crypto.Cipher

internal class BiometricDecryptionImpl(
    private val cipher: Cipher,
    private val biometricCipher: BiometricCipher,
    private val authorizedData: BiometricEncryptedData
) : BiometricDecryption {

    private val consumed = AtomicBoolean(false)

    override fun decrypt(data: BiometricEncryptedData): BiometricResult<ByteArray> {
        check(consumed.compareAndSet(false, true)) {
            "Biometric decryption authorization has already been used"
        }
        return decryptionResult {
            require(data == authorizedData) { "Encrypted data changed after biometric authorization" }
            biometricCipher.decrypt(cipher, data)
        }
    }

}
