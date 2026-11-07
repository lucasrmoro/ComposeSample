package br.com.composesample.structural.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AppSessionProviderImpl : AppSessionProvider {

    private val sessionExpired = MutableStateFlow(false)

    override val isSessionExpired = sessionExpired.asStateFlow()

    override fun expireSession() {
        sessionExpired.value = true
    }

}
