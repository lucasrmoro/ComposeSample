package br.com.composesample.featureA.list.model

sealed interface ListUiEffect {

    data object NavigateToForm : ListUiEffect
}
