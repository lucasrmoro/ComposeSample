package br.com.composesample.configuration

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.localization.LocalizationConfiguration

internal object AppConfigurationModule {

    private const val FALLBACK_COUNTRY_CODE = "BR"

    private val appConfigurationModule = dependencyModule {
        singleton { LocalizationConfiguration(fallbackCountryCode = FALLBACK_COUNTRY_CODE) }
    }

    fun init() = loadModules(appConfigurationModule)

}
