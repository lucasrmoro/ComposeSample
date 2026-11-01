package br.com.composesample.structural.biometric.di

import androidx.fragment.app.FragmentActivity
import br.com.composesample.structural.biometric.BiometricProvider
import br.com.composesample.structural.biometric.BiometricProviderFactory
import br.com.composesample.structural.core.ext.dispatcherMain
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton

object BiometricModule {

    private val biometricModule = dependencyModule {
        singleton { BiometricProviderFactory(mainDispatcher = dispatcherMain) }
        factory<BiometricProvider> { parameters ->
            get<BiometricProviderFactory>().create(parameters.get<FragmentActivity>())
        }
    }

    fun init() {
        loadModules(biometricModule)
    }

}
