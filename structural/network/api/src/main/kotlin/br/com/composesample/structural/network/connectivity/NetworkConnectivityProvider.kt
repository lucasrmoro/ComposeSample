package br.com.composesample.structural.network.connectivity

import androidx.lifecycle.LiveData

interface NetworkConnectivityProvider {

    val isInternetAvailable: LiveData<Boolean>

}