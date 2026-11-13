package br.com.composesample.configuration

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.localization.LocalizationConfiguration
import br.com.composesample.structural.video_player.VideoPlaybackConfiguration

internal object AppConfigurationModule {

    private const val FALLBACK_COUNTRY_CODE = "BR"
    private const val VIDEO_EMBED_ORIGIN = "https://composesample.app"

    private val appConfigurationModule = dependencyModule {
        singleton { VideoPlaybackConfiguration(origin = VIDEO_EMBED_ORIGIN) }
        singleton { LocalizationConfiguration(fallbackCountryCode = FALLBACK_COUNTRY_CODE) }
    }

    fun init() = loadModules(appConfigurationModule)

}
