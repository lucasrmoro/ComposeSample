package br.com.composesample.featureA.list.model

sealed interface ListUiEvent {
    data object LoadItems : ListUiEvent
    data object AddItemClicked : ListUiEvent
}
