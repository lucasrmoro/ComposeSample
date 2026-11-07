package br.com.composesample.structural.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class NetworkConnectivityProviderImpl(context: Context) : NetworkConnectivityProvider {

    private val manager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    private var currentNetwork = manager.activeNetwork

    private val state = MutableStateFlow(
        statusOf(currentNetwork, manager.getNetworkCapabilities(currentNetwork))
    )

    override val connectivity = state.asStateFlow()

    private var started = false

    private val callback =
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                synchronized(this@NetworkConnectivityProviderImpl) {
                    if (currentNetwork != network) {
                        currentNetwork = network
                        state.value = ConnectivityStatus.CHECKING
                    }
                }
            }

            override fun onLost(network: Network) {
                synchronized(this@NetworkConnectivityProviderImpl) {
                    if (currentNetwork == network) {
                        currentNetwork = null
                        state.value = ConnectivityStatus.OFFLINE
                    }
                }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                synchronized(this@NetworkConnectivityProviderImpl) {
                    if (currentNetwork == network) {
                        state.value = statusOf(network, capabilities)
                    }
                }
            }

        }

    @Synchronized
    override fun start() {
        if (started) return
        manager.registerDefaultNetworkCallback(callback)
        currentNetwork = manager.activeNetwork
        state.value = statusOf(currentNetwork, manager.getNetworkCapabilities(currentNetwork))
        started = true
    }

    private fun statusOf(
        network: Network?,
        capabilities: NetworkCapabilities?
    ): ConnectivityStatus = when {
        network == null -> ConnectivityStatus.OFFLINE
        capabilities == null -> ConnectivityStatus.CHECKING
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ->
            ConnectivityStatus.ONLINE
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) ||
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ->
            ConnectivityStatus.OFFLINE
        else -> ConnectivityStatus.CHECKING
    }

}
