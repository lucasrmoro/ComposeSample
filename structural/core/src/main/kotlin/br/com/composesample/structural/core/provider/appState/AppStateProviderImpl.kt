package br.com.composesample.structural.core.provider.appState

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

import br.com.composesample.structural.network.connectivity.ConnectivityStatus
import br.com.composesample.structural.network.connectivity.NetworkConnectivityProvider
import br.com.composesample.structural.session.AppSessionProvider
import br.com.composesample.structural.session.SessionStatus

internal class AppStateProviderImpl(
    private val appSessionProvider: AppSessionProvider,
    private val networkConnectivityProvider: NetworkConnectivityProvider
) : AppStateProvider {

    private val _state = MediatorLiveData<AppState>()

    override val state: LiveData<AppState> = _state

    init {
        _state.addSource(appSessionProvider.isSessionExpired) { updateState() }
        _state.addSource(networkConnectivityProvider.isInternetAvailable) { updateState() }
    }

    private fun updateState() {
        val internetAvailable = networkConnectivityProvider.isInternetAvailable.value ?: return
        val sessionExpired = appSessionProvider.isSessionExpired.value ?: false
        val updatedState = AppState(
            session = if (sessionExpired) SessionStatus.EXPIRED else SessionStatus.ACTIVE,
            connectivity = if (internetAvailable) ConnectivityStatus.ONLINE else ConnectivityStatus.OFFLINE
        )

        if (_state.value != updatedState) {
            _state.value = updatedState
        }
    }

}
