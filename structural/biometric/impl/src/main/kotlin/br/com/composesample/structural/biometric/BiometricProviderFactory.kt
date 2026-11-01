package br.com.composesample.structural.biometric

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineDispatcher

class BiometricProviderFactory(private val mainDispatcher: CoroutineDispatcher) {

    private val keyStore = BiometricKeyStore()

    fun create(activity: FragmentActivity): BiometricProvider = BiometricProviderImpl(
        authenticator = BiometricAuthenticator(activity),
        keyStore = keyStore,
        biometricCipher = BiometricCipher(),
        mainDispatcher = mainDispatcher
    )

}
