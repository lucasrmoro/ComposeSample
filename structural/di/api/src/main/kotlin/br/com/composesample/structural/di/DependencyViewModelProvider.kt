package br.com.composesample.structural.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

/** Android lifecycle bridge. Implementations retain instances in the owner's store, never in the DI singleton. */
interface DependencyViewModelProvider {

    /**
     * Resolves the ViewModel with the owner's saved-state handle and caller-provided constructor
     * arguments.
     */
    fun <T : ViewModel> get(
        owner: ViewModelStoreOwner,
        type: KClass<T>,
        parameters: DependencyParameters = DependencyParameters()
    ): T

}
