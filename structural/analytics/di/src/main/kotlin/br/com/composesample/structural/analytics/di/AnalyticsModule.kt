package br.com.composesample.structural.analytics.di

import br.com.composesample.structural.analytics.AnalyticsProvider
import br.com.composesample.structural.analytics.createAnalyticsProvider
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton

object AnalyticsModule {

    private val analyticsModule = dependencyModule {
        singleton<AnalyticsProvider> { createAnalyticsProvider(context = get()) }
    }

    fun init() {
        loadModules(analyticsModule)
    }

}
