package br.com.composesample.featureA.list.model

import br.com.composesample.featureA.model.SomeModel

data class ListUiState(
    val isLoading: Boolean = false,
    val items: List<SomeModel> = emptyList()
)
