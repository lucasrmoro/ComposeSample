package br.com.composesample.structural.core.provider.appState

import br.com.composesample.structural.network.connectivity.NetworkConnectivityProvider
import br.com.composesample.structural.session.AppSessionProvider

fun createAppStateProvider(
    appSessionProvider: AppSessionProvider,
    networkConnectivityProvider: NetworkConnectivityProvider
): AppStateProvider = AppStateProviderImpl(appSessionProvider, networkConnectivityProvider)
