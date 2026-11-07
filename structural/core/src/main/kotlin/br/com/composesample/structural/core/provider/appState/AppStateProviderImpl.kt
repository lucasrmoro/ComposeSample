package br.com.composesample.structural.core.provider.appState

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData

import br.com.composesample.structural.network.connectivity.NetworkConnectivityProvider
import br.com.composesample.structural.session.AppSessionProvider
import br.com.composesample.structural.session.SessionStatus

internal class AppStateProviderImpl(
    private val appSessionProvider: AppSessionProvider,
    private val networkConnectivityProvider: NetworkConnectivityProvider
) : AppStateProvider {

    private val connectivity = networkConnectivityProvider.connectivity.asLiveData()

    private val _state = MediatorLiveData<AppState>()

    override val state: LiveData<AppState> = _state

    init {
        _state.addSource(appSessionProvider.isSessionExpired) { updateState() }
        _state.addSource(connectivity) { updateState() }
    }

    private fun updateState() {
        val sessionExpired = appSessionProvider.isSessionExpired.value ?: false
        val updatedState = AppState(
            session = if (sessionExpired) SessionStatus.EXPIRED else SessionStatus.ACTIVE,
            connectivity = networkConnectivityProvider.connectivity.value
        )

        if (_state.value != updatedState) {
            _state.value = updatedState
        }
    }

}
