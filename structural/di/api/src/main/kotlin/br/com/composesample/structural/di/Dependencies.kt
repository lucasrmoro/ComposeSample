package br.com.composesample.structural.di

import kotlin.reflect.KClass

/** Process-level resolution entrypoint for framework-created components. Prefer constructor injection elsewhere. */
object Dependencies : DependencyResolver {

    @Volatile
    private var container: DependencyContainer? = null

    @Synchronized
    fun start(backend: DependencyContainer, configure: () -> Unit = {}): DependencyResolver {
        check(container == null) { "Dependency injection is already started" }
        container = backend
        try {
            configure()
        } catch (exception: Throwable) {
            stop()
            throw exception
        }
        return this
    }

    @Synchronized
    fun stop() {
        val previous = container
        container = null
        previous?.close()
    }

    fun loadModules(modules: List<DependencyModule>) {
        requireContainer().loadModules(modules)
    }

    override fun <T : Any> get(
        type: KClass<T>,
        qualifier: DependencyQualifier?,
        parameters: DependencyParameters
    ): T = requireContainer().get(type, qualifier, parameters)

    private fun requireContainer(): DependencyContainer =
        checkNotNull(container) { "Dependency injection has not been started" }

}

fun loadModules(vararg modules: DependencyModule) = Dependencies.loadModules(modules.toList())

fun stopDependencyInjection() = Dependencies.stop()

inline fun <reified T : Any> get(
    qualifier: DependencyQualifier? = null,
    noinline parameters: (() -> DependencyParameters)? = null
): T = Dependencies.get(qualifier, parameters)

inline fun <reified T : Any> inject(
    qualifier: DependencyQualifier? = null,
    noinline parameters: (() -> DependencyParameters)? = null
): Lazy<T> = lazy { get<T>(qualifier, parameters) }
