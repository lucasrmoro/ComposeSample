package br.com.composesample.structural.localization

import kotlinx.coroutines.flow.StateFlow

/** Observes configuration changes after [start] is explicitly called by the application. */
interface AppRegionProvider {

    val region: StateFlow<AppRegion>

    fun start()

}
