package br.com.composesample.example.viewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class StateHolder {

    abstract val uiState: StateFlow<Unit>

}

class ExampleViewModel : StateHolder() {

    private var _uiState = MutableStateFlow(Unit)
    override val uiState: StateFlow<Unit> = _uiState.asStateFlow()

}
