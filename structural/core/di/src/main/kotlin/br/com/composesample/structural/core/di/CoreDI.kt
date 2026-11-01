package br.com.composesample.structural.core.di

import br.com.composesample.structural.core.provider.appState.AppStateProvider
import br.com.composesample.structural.core.provider.appState.createAppStateProvider
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object CoreModule {

    private val coreModule = dependencyModule {
        singleton<CoroutineDispatcher>(DispatcherIO) { Dispatchers.IO }
        singleton<CoroutineDispatcher>(DispatcherMain) { Dispatchers.Main }
        singleton<CoroutineDispatcher>(DispatcherDefault) { Dispatchers.Default }
        singleton<AppStateProvider> {
            createAppStateProvider(appSessionProvider = get(), networkConnectivityProvider = get())
        }
    }

    fun init() {
        loadModules(coreModule)
    }

}
