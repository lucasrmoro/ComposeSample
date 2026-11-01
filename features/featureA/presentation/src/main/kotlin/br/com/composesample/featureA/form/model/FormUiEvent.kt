package br.com.composesample.featureA.form.model

sealed interface FormUiEvent {

    data class MessageChanged(val value: String) : FormUiEvent
    data object SaveClicked : FormUiEvent
}
