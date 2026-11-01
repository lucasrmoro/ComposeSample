package br.com.composesample.structural.di

import kotlin.reflect.KClass
import org.koin.core.module.Module
import org.koin.dsl.binds

internal class KoinDependencyRegistry(private val module: Module) : DependencyRegistry {

    override fun <T : Any> singleton(
        type: KClass<T>,
        qualifier: DependencyQualifier?,
        definition: DependencyResolver.(DependencyParameters) -> T
    ) {
        module.single<Any>(bindingQualifier(type, qualifier)) { parameters ->
            definition(KoinDependencyResolver(this), DependencyParameters(*parameters.values.toTypedArray()))
        }.binds(arrayOf(type))
    }

    override fun <T : Any> factory(
        type: KClass<T>,
        qualifier: DependencyQualifier?,
        definition: DependencyResolver.(DependencyParameters) -> T
    ) {
        module.factory<Any>(bindingQualifier(type, qualifier)) { parameters ->
            definition(KoinDependencyResolver(this), DependencyParameters(*parameters.values.toTypedArray()))
        }.binds(arrayOf(type))
    }

}
