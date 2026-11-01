package br.com.composesample.structural.biometric

import java.util.concurrent.atomic.AtomicBoolean
import javax.crypto.Cipher

internal class BiometricEncryptionImpl(
    private val cipher: Cipher,
    private val biometricCipher: BiometricCipher
) : BiometricEncryption {

    private val consumed = AtomicBoolean(false)

    override fun encrypt(data: ByteArray): BiometricResult<BiometricEncryptedData> {
        check(consumed.compareAndSet(false, true)) {
            "Biometric encryption authorization has already been used"
        }
        return encryptionResult {
            biometricCipher.encrypt(cipher, data)
        }
    }

}
