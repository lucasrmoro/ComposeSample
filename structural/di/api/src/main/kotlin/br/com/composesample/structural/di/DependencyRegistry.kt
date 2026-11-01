package br.com.composesample.structural.di

import kotlin.reflect.KClass

/** Declares constructor factories without exposing a specific container. Later registrations override earlier ones. */
interface DependencyRegistry {

    /** Retains one instance per type and qualifier until the container closes or the binding is replaced. */
    fun <T : Any> singleton(
        type: KClass<T>,
        qualifier: DependencyQualifier? = null,
        definition: DependencyResolver.(DependencyParameters) -> T
    )

    /** Creates a new instance for every resolution. Runtime arguments are not retained by the container. */
    fun <T : Any> factory(
        type: KClass<T>,
        qualifier: DependencyQualifier? = null,
        definition: DependencyResolver.(DependencyParameters) -> T
    )

}

inline fun <reified T : Any> DependencyRegistry.singleton(
    qualifier: DependencyQualifier? = null,
    noinline definition: DependencyResolver.(DependencyParameters) -> T
) = singleton(T::class, qualifier, definition)

inline fun <reified T : Any> DependencyRegistry.factory(
    qualifier: DependencyQualifier? = null,
    noinline definition: DependencyResolver.(DependencyParameters) -> T
) = factory(T::class, qualifier, definition)
