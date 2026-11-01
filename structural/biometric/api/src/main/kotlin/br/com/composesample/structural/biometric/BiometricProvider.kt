package br.com.composesample.structural.biometric

/** Provides strong biometric status and single-use cryptographic authorizations for one screen. */
interface BiometricProvider {

    /**
     * Returns the current device status without opening a prompt or accessing cryptographic keys.
     * Availability does not indicate whether biometrics are enabled in the application.
     */
    fun getStatus(): BiometricStatus

    /**
     * Authorizes encryption of one payload, without serializing or persisting data.
     *
     * @param prompt Text displayed by the system biometric prompt.
     * @return A single-use encryption authorization or a biometric/cryptographic failure.
     * @throws IllegalStateException If the screen is not resumed.
     * @throws kotlinx.coroutines.CancellationException If the calling coroutine or screen is cancelled.
     */
    suspend fun enroll(prompt: BiometricPromptConfig): BiometricResult<BiometricEncryption>

    /**
     * Authorizes decryption of the supplied payload, without reading preferences.
     *
     * @param data The exact encrypted package that will be decrypted after authorization.
     * @param prompt Text displayed by the system biometric prompt.
     * @return A single-use decryption authorization bound to [data], or a failure.
     * @throws IllegalStateException If the screen is not resumed.
     * @throws kotlinx.coroutines.CancellationException If the calling coroutine or screen is cancelled.
     */
    suspend fun authenticate(
        data: BiometricEncryptedData,
        prompt: BiometricPromptConfig
    ): BiometricResult<BiometricDecryption>

}
