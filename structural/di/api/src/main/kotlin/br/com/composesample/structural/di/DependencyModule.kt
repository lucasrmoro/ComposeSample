package br.com.composesample.structural.di

/** A group of registrations applied in declaration order when explicitly loaded by the application. */
fun interface DependencyModule {

    fun register(registry: DependencyRegistry)

}

fun dependencyModule(definition: DependencyRegistry.() -> Unit): DependencyModule =
    DependencyModule { registry -> registry.definition() }
