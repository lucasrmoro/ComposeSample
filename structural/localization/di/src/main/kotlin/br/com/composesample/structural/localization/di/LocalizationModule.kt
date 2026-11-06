package br.com.composesample.structural.localization.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.localization.AppRegionProvider
import br.com.composesample.structural.localization.createAppRegionProvider

object LocalizationModule {

    private val localizationModule = dependencyModule {
        singleton<AppRegionProvider> {
            createAppRegionProvider(applicationContext = get(), localizationConfiguration = get())
        }
    }

    fun init() = loadModules(localizationModule)

}
