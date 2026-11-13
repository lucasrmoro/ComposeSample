package br.com.composesample.structural.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Resolves a ViewModel through the application DI contract and stores it in the active
 * Compose navigation owner, so each NavBackStackEntry retains its own ViewModel instance.
 * Additional [parameters] are passed to its DI factory after the automatically supplied
 * `SavedStateHandle`.
 */
@Composable
inline fun <reified T : ViewModel> dependencyViewModel(
    owner: ViewModelStoreOwner? = LocalViewModelStoreOwner.current,
    parameters: DependencyParameters = DependencyParameters()
): T {
    val viewModelStoreOwner = checkNotNull(owner) {
        "A ViewModelStoreOwner must be provided by the Compose host"
    }
    return remember(viewModelStoreOwner, parameters.values) {
        get<DependencyViewModelProvider>().get(
            owner = viewModelStoreOwner,
            type = T::class,
            parameters = parameters
        )
    }
}
