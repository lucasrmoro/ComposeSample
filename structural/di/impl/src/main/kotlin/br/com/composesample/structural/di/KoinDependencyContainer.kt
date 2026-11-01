package br.com.composesample.structural.di

import kotlin.reflect.KClass
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal class KoinDependencyContainer : DependencyContainer {

    private val application = koinApplication {}

    override fun loadModules(modules: List<DependencyModule>) {
        val registrations = module {
            val registry = KoinDependencyRegistry(this)
            modules.forEach { it.register(registry) }
        }
        application.koin.loadModules(listOf(registrations))
    }

    override fun <T : Any> get(
        type: KClass<T>,
        qualifier: DependencyQualifier?,
        parameters: DependencyParameters
    ): T = application.koin.get(
        clazz = type,
        qualifier = bindingQualifier(type, qualifier),
        parameters = parameters.koinParameters()
    )

    override fun close() {
        application.close()
    }

}

fun startDependencyInjection(configure: () -> Unit = {}): DependencyResolver =
    Dependencies.start(KoinDependencyContainer(), configure)
