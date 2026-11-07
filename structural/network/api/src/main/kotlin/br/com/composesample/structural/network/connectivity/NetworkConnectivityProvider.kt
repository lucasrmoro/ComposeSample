package br.com.composesample.structural.network.connectivity

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.StateFlow

/** Internet validation state; observing does not register callbacks until [start]. */
interface NetworkConnectivityProvider {

    val isInternetAvailable: LiveData<Boolean>
    val connectivity: StateFlow<ConnectivityStatus>

    fun start()

}
