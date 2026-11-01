package br.com.composesample.structural.navigation.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.navigation.core.NavigationRegistry
import br.com.composesample.structural.navigation.NavigationRegistryFactory

object NavigationModule {

    private val navigationModule = dependencyModule {
        singleton { NavigationRegistryFactory() }
        singleton<NavigationRegistry> { get<NavigationRegistryFactory>().registry }
    }

    fun init() {
        loadModules(navigationModule)
    }

}
