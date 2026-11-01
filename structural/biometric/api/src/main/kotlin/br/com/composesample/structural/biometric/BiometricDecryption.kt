package br.com.composesample.structural.biometric

/** A single-use decryption operation authorized for one encrypted package. */
interface BiometricDecryption {

    /**
     * Decrypts the exact package originally supplied to [BiometricProvider.authenticate].
     *
     * @param data The encrypted package bound to this authorization.
     * @return Plaintext bytes, or a failure if the package differs, is corrupted, or the key cannot be used.
     * @throws IllegalStateException If this authorization has already been consumed.
     */
    fun decrypt(data: BiometricEncryptedData): BiometricResult<ByteArray>

}
