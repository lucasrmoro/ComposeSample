package br.com.composesample.structural.remote_config.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.remote_config.RemoteConfigProvider
import br.com.composesample.structural.remote_config.createRemoteConfigProvider

object RemoteConfigModule {

    private val remoteConfigModule = dependencyModule {
        singleton<RemoteConfigProvider> { createRemoteConfigProvider(context = get()) }
    }

    fun init() {
        loadModules(remoteConfigModule)
    }

}
