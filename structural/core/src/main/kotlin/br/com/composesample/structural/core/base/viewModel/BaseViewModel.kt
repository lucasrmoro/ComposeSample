package br.com.composesample.structural.core.base.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Event, Effect> : ViewModel(), UiViewModel<State, Effect> {

    private val effectChannel = Channel<Effect>(Channel.UNLIMITED)

    override val uiEffect: Flow<Effect> = effectChannel.receiveAsFlow()

    abstract fun sendEvent(event: Event)

    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(block = block)
    }

    protected fun sendEffect(effect: Effect) {
        effectChannel.trySend(effect)
    }

    override fun onCleared() {
        effectChannel.close()
        viewModelScope.cancel()
    }

}
