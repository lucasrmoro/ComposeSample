package br.com.composesample.structural.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class NetworkConnectivityProviderImpl(context: Context) : NetworkConnectivityProvider {

    private val manager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    private val state = MutableStateFlow(currentStatus())

    private val available = MutableLiveData(state.value == ConnectivityStatus.ONLINE)

    override val isInternetAvailable: LiveData<Boolean> = available

    override val connectivity = state.asStateFlow()

    private var started = false

    private val callback =
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) = update()

            override fun onLost(network: Network) = update()

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) = update()

        }

    override fun start() {
        if (started) return
        started = true
        manager.registerDefaultNetworkCallback(callback)
        update()
    }

    private fun update() {
        state.value = currentStatus()
        available.postValue(state.value == ConnectivityStatus.ONLINE)
    }

    private fun currentStatus(): ConnectivityStatus {
        val caps = manager.getNetworkCapabilities(manager.activeNetwork)
        return if (caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true)
            ConnectivityStatus.ONLINE
        else ConnectivityStatus.OFFLINE
    }

}
