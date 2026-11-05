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
    fun Init() {
        val currentConsumeEffect by rememberUpdatedState(::consumeEffect)

        LaunchedEffect(viewModel.uiEffect) {
            viewModel.uiEffect.collect { effect -> currentConsumeEffect(effect) }
        }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Content(uiState)
    }

    @Composable
    protected abstract fun Content(uiState: State)

    protected abstract fun consumeEffect(effect: Effect)
}
