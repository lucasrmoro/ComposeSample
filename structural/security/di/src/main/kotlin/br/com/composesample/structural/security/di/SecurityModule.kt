package br.com.composesample.structural.security.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.security.hashing.HashingProvider
import br.com.composesample.structural.security.hashing.createHashingProvider

object SecurityModule {

    private val securityModule = dependencyModule {
        factory<HashingProvider> { createHashingProvider() }
    }

    fun init() = loadModules(securityModule)

}
