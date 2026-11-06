package br.com.composesample.structural.localization

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implements [ComponentCallbacks] to refresh the exposed region when Android resource
 * configuration changes, including the application language. Registration uses the application
 * context and begins only when [start] is called explicitly.
 */
internal class AndroidAppRegionProvider(
    private val applicationContext: Context,
    private val localizationConfiguration: LocalizationConfiguration
) : AppRegionProvider, ComponentCallbacks {

    private val state = MutableStateFlow(readRegion())

    override val region = state.asStateFlow()

    private var started = false

    override fun start() {
        if (started) return
        started = true
        state.value = readRegion()
        applicationContext.registerComponentCallbacks(this)
    }

    private fun readRegion(): AppRegion {
        val locale = applicationContext.resources.configuration.locales[0] ?: Locale.getDefault()
        return AppRegion(
            languageTag = locale.toLanguageTag(),
            countryCode = Locale.getDefault().country.ifBlank { localizationConfiguration.fallbackCountryCode },
            timeZoneId = TimeZone.getDefault().id
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        state.value = readRegion()
    }

    /**
     * No cleanup is needed: this provider retains only the current region state and does not own
     * caches or memory-intensive resources. The method is required by [ComponentCallbacks].
     */
    override fun onLowMemory() = Unit

}

/** Creates the provider using the application context registered by the DI service. */
fun createAppRegionProvider(
    applicationContext: Context,
    localizationConfiguration: LocalizationConfiguration
): AppRegionProvider = AndroidAppRegionProvider(
    applicationContext = applicationContext,
    localizationConfiguration = localizationConfiguration
)
