package br.com.composesample.structural.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import javax.crypto.Cipher
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

internal class BiometricAuthenticator(private val activity: FragmentActivity) {

    private val executor = ContextCompat.getMainExecutor(activity)

    private var currentRequest: BiometricAuthenticationRequest? = null

    val isAuthenticating: Boolean
        get() = currentRequest != null

    private val prompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                complete(authenticationResult(result))
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                complete(BiometricResult.Failure(errorCode.toBiometricError()))
            }

        }
    )

    fun getStatus(): BiometricStatus = BiometricManager.from(activity)
        .canAuthenticate(BIOMETRIC_STRONG).toBiometricStatus()

    fun screenIsReady(): Boolean = !activity.isFinishing && !activity.isDestroyed &&
        activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    suspend fun authenticate(
        cipher: Cipher,
        config: BiometricPromptConfig
    ): BiometricResult<Cipher> = suspendCancellableCoroutine { continuation ->
        val info = createPromptInfo(config)
        val request = registerRequest(continuation)
        if (request.isActive) {
            launchAuthentication(cipher, info)
        } else {
            complete(BiometricResult.Failure(BiometricError.CANCELLED))
        }
    }

    private fun createPromptInfo(config: BiometricPromptConfig): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(config.title)
            .setSubtitle(config.subtitle)
            .setNegativeButtonText(config.cancelText)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()

    private fun registerRequest(
        continuation: CancellableContinuation<BiometricResult<Cipher>>
    ): BiometricAuthenticationRequest {
        val request = BiometricAuthenticationRequest(continuation)
        currentRequest = request
        request.invokeOnCancellation { cancelOnMainThread(request) }
        return request
    }

    private fun cancelOnMainThread(request: BiometricAuthenticationRequest) {
        executor.execute {
            if (currentRequest === request && request.cancel()) {
                prompt.cancelAuthentication()
            }
        }
    }

    private fun launchAuthentication(cipher: Cipher, info: BiometricPrompt.PromptInfo) {
        try {
            prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
        } catch (exception: RuntimeException) {
            currentRequest = null
            throw exception
        }
    }

    private fun authenticationResult(result: BiometricPrompt.AuthenticationResult): BiometricResult<Cipher> {
        val cipher = result.cryptoObject?.cipher
        return when {
            cipher == null -> BiometricResult.Failure(BiometricError.AUTHENTICATION_FAILED)
            else -> BiometricResult.Success(cipher)
        }
    }

    private fun complete(result: BiometricResult<Cipher>) {
        val request = currentRequest
        currentRequest = null
        request?.complete(result)
    }

}
