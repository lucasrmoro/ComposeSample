package br.com.composesample.featureA.form.viewModel

import br.com.composesample.featureA.form.model.FormUiEffect
import br.com.composesample.featureA.form.model.FormUiEvent
import br.com.composesample.featureA.form.model.FormUiState
import br.com.composesample.structural.core.base.viewModel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormViewModel : BaseViewModel<FormUiState, FormUiEvent, FormUiEffect>() {

    private val _uiState = MutableStateFlow(FormUiState())
    override val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    override fun sendEvent(event: FormUiEvent) {
        when (event) {
            is FormUiEvent.MessageChanged -> _uiState.update { it.copy(message = event.value) }
            FormUiEvent.SaveClicked -> save()
        }
    }

    private fun save() {
        _uiState.update { it.copy(isSaving = true) }
        sendEffect(FormUiEffect.NavigateBack)
        _uiState.update { it.copy(isSaving = false) }
    }

}
