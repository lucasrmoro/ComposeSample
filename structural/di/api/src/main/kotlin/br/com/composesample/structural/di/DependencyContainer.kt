package br.com.composesample.structural.di

/** Runtime backend boundary. Implementations own instance caching and resolution; callers own startup and shutdown. */
interface DependencyContainer : DependencyResolver {

    fun loadModules(modules: List<DependencyModule>)

    /** Releases retained instances. Lifecycle-sensitive resources remain the responsibility of their owners. */
    fun close()

}
