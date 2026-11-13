package br.com.composesample.stream.catalog.model

import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType


sealed interface CatalogUiEvent {

    data object Start : CatalogUiEvent

    data class SelectType(val type: MediaType) : CatalogUiEvent

    data class SearchChanged(val text: String) : CatalogUiEvent

    data object SubmitSearch : CatalogUiEvent

    data object OpenGenres : CatalogUiEvent

    data class OpenCollection(val kind: CollectionKind) : CatalogUiEvent

    data class OpenDetails(val id: MediaId) : CatalogUiEvent

    data object Refresh : CatalogUiEvent

    data object LoadMoreSearch : CatalogUiEvent

}
