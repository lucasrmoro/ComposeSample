package br.com.composesample.structural.navigation

import br.com.composesample.structural.navigation.core.FeatureEntryPoint
import br.com.composesample.structural.navigation.core.NavigationRegistry
import java.util.concurrent.ConcurrentHashMap

internal class NavigationRegistryImpl : NavigationRegistry {

    private val lock = Any()
    private val entryPointsByRoute = linkedMapOf<String, FeatureEntryPoint>()
    private var startDestinationRoute: String? = null

    override val entryPoints: List<FeatureEntryPoint>
        get() = synchronized(lock) { entryPointsByRoute.values.toList() }

    override val startDestination: String
        get() = synchronized(lock) {
            checkNotNull(startDestinationRoute) { "No start destination has been registered" }
        }

    override fun register(entryPoint: FeatureEntryPoint, isStartDestination: Boolean) {
        synchronized(lock) {
            check(entryPoint.route !in entryPointsByRoute) {
                "Feature route already registered: ${entryPoint.route}"
            }
            if (isStartDestination) {
                check(startDestinationRoute == null) { "A start destination is already registered" }
                startDestinationRoute = entryPoint.route
            }
            entryPointsByRoute[entryPoint.route] = entryPoint
        }
    }

}
