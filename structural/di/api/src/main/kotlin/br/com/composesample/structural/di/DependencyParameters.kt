package br.com.composesample.structural.di

import kotlin.reflect.KClass

/** Immutable per-resolution arguments. Duplicate types are resolved in insertion order. */
class DependencyParameters(vararg values: Any?) {

    val values: List<Any?> = values.toList()

    fun <T : Any> get(type: KClass<T>): T {
        val value = values.firstOrNull { type.javaObjectType.isInstance(it) }
        return type.javaObjectType.cast(value)
            ?: error("Missing parameter: ${type.qualifiedName}")
    }

}

inline fun <reified T : Any> DependencyParameters.get(): T = get(T::class)

fun parametersOf(vararg values: Any?): DependencyParameters = DependencyParameters(*values)
