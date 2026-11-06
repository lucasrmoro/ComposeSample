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
internal class AndroidAppRegionProvider(context: Context) : AppRegionProvider, ComponentCallbacks {

    private val context = context.applicationContext

    private val state = MutableStateFlow(readRegion())

    override val region = state.asStateFlow()

    private var started = false

    override fun start() {
        if (started) return
        started = true
        state.value = readRegion()
        context.registerComponentCallbacks(this)
    }

    private fun readRegion(): AppRegion {
        val locale = context.resources.configuration.locales[0] ?: Locale.getDefault()
        return AppRegion(
            languageTag = locale.toLanguageTag(),
            countryCode = Locale.getDefault().country.ifBlank { FALLBACK_COUNTRY },
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

    private companion object {
        const val FALLBACK_COUNTRY = "BR"
    }
}

fun createAppRegionProvider(context: Context): AppRegionProvider = AndroidAppRegionProvider(context)
