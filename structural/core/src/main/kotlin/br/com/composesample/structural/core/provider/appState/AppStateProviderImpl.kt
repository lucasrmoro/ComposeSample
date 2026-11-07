package br.com.composesample.structural.core.provider.appState

import br.com.composesample.structural.network.connectivity.NetworkConnectivityProvider
import br.com.composesample.structural.session.AppSessionProvider
import br.com.composesample.structural.session.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class AppStateProviderImpl(
    appSessionProvider: AppSessionProvider,
    networkConnectivityProvider: NetworkConnectivityProvider
) : AppStateProvider {

    override val state: Flow<AppState> = combine(
        appSessionProvider.isSessionExpired,
        networkConnectivityProvider.connectivity
    ) { sessionExpired, connectivity ->
        AppState(
            session = if (sessionExpired) SessionStatus.EXPIRED else SessionStatus.ACTIVE,
            connectivity = connectivity
        )
    }.distinctUntilChanged()

}
