package br.com.composesample.structural.di

import android.app.Application
import android.content.Context

fun startDependencyInjection(context: Context, configure: () -> Unit = {}): DependencyResolver =
    startDependencyInjection {
        val appContext = context.applicationContext
        loadModules(
            dependencyModule {
                singleton<DependencyViewModelProvider> { AndroidDependencyViewModelProvider() }
                singleton<Context> { appContext }
                singleton<Application> { appContext as Application }
            }
        )
        configure()
    }
