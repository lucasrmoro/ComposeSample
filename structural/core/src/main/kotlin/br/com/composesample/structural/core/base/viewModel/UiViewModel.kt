package br.com.composesample.structural.core.base.viewModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/** Exposes state and one-off UI effects to a screen. */
interface UiViewModel<State, Effect> {

    val uiState: StateFlow<State>
    val uiEffect: Flow<Effect>

}
