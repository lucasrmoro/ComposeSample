package br.com.composesample.structural.navigation.core

/** Stores feature entry points for the application lifetime without retaining screen state. */
interface NavigationRegistry {

    /** Registered feature entry points used to build the app-level navigation graph. */
    val entryPoints: List<FeatureEntryPoint>

    /** Route of the feature registered as the app start destination. */
    val startDestination: String

    /** Registers a feature during application setup; duplicate routes and start destinations are rejected. */
    fun register(entryPoint: FeatureEntryPoint, isStartDestination: Boolean = false)

}
