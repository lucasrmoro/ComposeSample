package br.com.composesample.structural.designsystem.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.composesample.structural.core.base.viewModel.UiViewModel

/** Base for feature screen classes that collect state and handle one-off UI effects. */
abstract class BaseScreen<State, Effect>(private val viewModel: UiViewModel<State, Effect>) {

    @Composable
    fun Start() {
        val currentProcessEffect by rememberUpdatedState(::processEffect)

        LaunchedEffect(viewModel.uiEffect) {
            viewModel.uiEffect.collect { effect -> currentProcessEffect(effect) }
        }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Content(uiState)
    }

    @Composable
    protected abstract fun Content(uiState: State)

    protected abstract fun processEffect(effect: Effect)
}
