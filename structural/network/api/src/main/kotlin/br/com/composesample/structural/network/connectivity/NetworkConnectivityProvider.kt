package br.com.composesample.structural.network.connectivity

import kotlinx.coroutines.flow.StateFlow

/** Internet validation state; observing does not register callbacks until [start]. */
interface NetworkConnectivityProvider {

    /**
     * Current default-network validation state, initialized from an Android network snapshot.
     * [ConnectivityStatus.CHECKING] distinguishes unconfirmed validation from offline status.
     */
    val connectivity: StateFlow<ConnectivityStatus>

    /** Starts application-lifetime observation; repeated calls do not register additional callbacks. */
    fun start()

}
