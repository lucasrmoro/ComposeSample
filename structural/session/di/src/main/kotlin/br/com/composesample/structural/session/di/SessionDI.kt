package br.com.composesample.structural.session.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.session.AppSessionProvider
import br.com.composesample.structural.session.createAppSessionProvider

object SessionModule {

    private val sessionModule = dependencyModule {
        singleton<AppSessionProvider> { createAppSessionProvider() }
    }

    fun init() {
        loadModules(sessionModule)
    }

}
