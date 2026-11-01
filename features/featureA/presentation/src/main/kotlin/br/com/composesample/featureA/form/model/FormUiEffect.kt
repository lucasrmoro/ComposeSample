package br.com.composesample.featureA.form.model

sealed interface FormUiEffect {

    data object NavigateBack : FormUiEffect
}
