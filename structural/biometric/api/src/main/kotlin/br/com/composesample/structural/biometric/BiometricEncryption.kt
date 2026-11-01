package br.com.composesample.structural.biometric

/** A single-use encryption operation authorized by the system biometric prompt. */
interface BiometricEncryption {

    /**
     * Encrypts [data] and returns an opaque package containing the IV and authenticated ciphertext.
     *
     * @param data The complete plaintext package to encrypt in one operation.
     * @return The encrypted package, or the key/encryption failure that prevented its creation.
     * @throws IllegalStateException If this authorization has already been consumed.
     */
    fun encrypt(data: ByteArray): BiometricResult<BiometricEncryptedData>

}
