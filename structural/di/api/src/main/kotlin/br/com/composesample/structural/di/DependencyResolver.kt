package br.com.composesample.structural.di

import kotlin.reflect.KClass

/** Resolves bindings by type and qualifier, passing runtime arguments only for the current request. */
interface DependencyResolver {

    /** Missing bindings fail at runtime. Singleton arguments apply only when the instance is first created. */
    fun <T : Any> get(
        type: KClass<T>,
        qualifier: DependencyQualifier? = null,
        parameters: DependencyParameters = DependencyParameters()
    ): T

}

inline fun <reified T : Any> DependencyResolver.get(
    qualifier: DependencyQualifier? = null,
    noinline parameters: (() -> DependencyParameters)? = null
): T = get(T::class, qualifier, parameters?.invoke() ?: DependencyParameters())
