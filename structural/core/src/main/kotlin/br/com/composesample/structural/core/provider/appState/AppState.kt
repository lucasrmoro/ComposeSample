package br.com.composesample.structural.core.provider.appState

import br.com.composesample.structural.network.connectivity.ConnectivityStatus
import br.com.composesample.structural.session.SessionStatus

data class AppState(
    val session: SessionStatus,
    val connectivity: ConnectivityStatus
)
