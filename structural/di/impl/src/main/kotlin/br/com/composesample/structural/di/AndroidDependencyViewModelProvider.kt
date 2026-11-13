package br.com.composesample.structural.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlin.reflect.KClass

internal class AndroidDependencyViewModelProvider : DependencyViewModelProvider {

    override fun <T : ViewModel> get(
        owner: ViewModelStoreOwner,
        type: KClass<T>,
        parameters: DependencyParameters,
    ): T {
        val factory = viewModelFactory {
            addInitializer(type) {
                Dependencies.get(
                    type,
                    parameters =
                        parametersOf(createSavedStateHandle(), *parameters.values.toTypedArray()),
                )
            }
        }
        return ViewModelProvider(owner, factory)[type.java]
    }

}
