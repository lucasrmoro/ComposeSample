package br.com.composesample.structural.logger.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.logger.Logger
import br.com.composesample.structural.logger.createLogger

object LoggerModule {

    private val loggerModule = dependencyModule {
        singleton<Logger> { createLogger() }
    }

    fun init() {
        loadModules(loggerModule)
    }

}
