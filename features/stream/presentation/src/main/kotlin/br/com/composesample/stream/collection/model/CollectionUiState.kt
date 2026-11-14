package br.com.composesample.stream.collection.model

import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.structural.core.text.UiText

data class CollectionUiState(
    val query: CatalogQuery,
    val page: CatalogPage = CatalogPage(),
    val loading: Boolean = true,
    val paging: Boolean = false,
    val error: UiText? = null
)
