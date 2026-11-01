package br.com.composesample.structural.di

import kotlin.reflect.KClass
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

internal class KoinDependencyResolver(private val scope: Scope) : DependencyResolver {

    override fun <T : Any> get(
        type: KClass<T>,
        qualifier: DependencyQualifier?,
        parameters: DependencyParameters
    ): T = scope.get(
        clazz = type,
        qualifier = bindingQualifier(type, qualifier),
        parameters = parameters.koinParameters()
    )

}

internal fun bindingQualifier(type: KClass<*>, qualifier: DependencyQualifier?): Qualifier =
    named("${type.java.name}#${qualifier?.value?.let { "${it.length}:$it" } ?: "default"}")

internal fun DependencyParameters.koinParameters(): ParametersDefinition? =
    if (values.isEmpty()) null else ({ org.koin.core.parameter.parametersOf(*values.toTypedArray()) })
