package br.com.composesample.configuration

import br.com.composesample.structural.assets_cache.AssetsCacheConfiguration
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.localization.LocalizationConfiguration
import br.com.composesample.structural.video_player.VideoPlaybackConfiguration
import java.time.Duration

internal object AppConfigurationModule {

    private const val FALLBACK_COUNTRY_CODE = "BR"

    private const val ASSETS_CACHE_DIRECTORY = "stream-images"
    private const val ASSETS_CACHE_MAXIMUM_BYTES = 200L * 1024 * 1024 // 200 MiB
    private const val ASSETS_CACHE_MAXIMUM_AGE_DAYS = 30L
    private const val VIDEO_EMBED_ORIGIN = "https://composesample.app"

    private val appConfigurationModule = dependencyModule {
        singleton { VideoPlaybackConfiguration(origin = VIDEO_EMBED_ORIGIN) }
        singleton { LocalizationConfiguration(fallbackCountryCode = FALLBACK_COUNTRY_CODE) }
        singleton {
            AssetsCacheConfiguration(
                directory = ASSETS_CACHE_DIRECTORY,
                maximumBytes = ASSETS_CACHE_MAXIMUM_BYTES,
                maximumAgeMillis = Duration.ofDays(ASSETS_CACHE_MAXIMUM_AGE_DAYS).toMillis()
            )
        }
    }

    fun init() = loadModules(appConfigurationModule)

}
