package br.com.composesample.structural.session

import androidx.lifecycle.LiveData

interface AppSessionProvider {

    val isSessionExpired: LiveData<Boolean>

    fun expireSession()

}