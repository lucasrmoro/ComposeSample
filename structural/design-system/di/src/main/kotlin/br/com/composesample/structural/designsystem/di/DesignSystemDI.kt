package br.com.composesample.structural.designsystem.di

import androidx.fragment.app.FragmentActivity
import br.com.composesample.structural.biometric.BiometricProvider
import br.com.composesample.structural.designsystem.biometric.createLifecycleBiometricProvider
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.parametersOf
import kotlinx.coroutines.CoroutineScope

object DesignSystemModule {

    private val designSystemModule = dependencyModule {
        factory<BiometricProvider>(ScreenBiometricProvider) { parameters ->
            createLifecycleBiometricProvider(
                delegate = get { parametersOf(parameters.get<FragmentActivity>()) },
                screenScope = parameters.get<CoroutineScope>()
            )
        }
    }

    fun init() {
        loadModules(designSystemModule)
    }

}
