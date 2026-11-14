package br.com.composesample.stream.model.catalog

import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.structural.core.text.UiText

data class CatalogUiState(
    val type: MediaType = MediaType.MOVIE,
    val searchText: String = "",
    val sections: Map<CollectionKind, CatalogSection> = emptyMap(),
    val search: CatalogSection = CatalogSection(refreshing = false)
) {

    data class CatalogSection(
        val page: CatalogPage = CatalogPage(),
        val refreshing: Boolean = true,
        val error: UiText? = null
    )

}
