package br.com.composesample.structural.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

/** Android lifecycle bridge. Implementations retain instances in the owner's store, never in the DI singleton. */
interface DependencyViewModelProvider {

    /** Uses the owner's creation extras to restore SavedStateHandle and resolve constructor dependencies. */
    fun <T : ViewModel> get(owner: ViewModelStoreOwner, type: KClass<T>): T

}
