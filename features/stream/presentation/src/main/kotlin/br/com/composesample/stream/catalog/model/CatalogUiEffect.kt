package br.com.composesample.stream.catalog.model

import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType

sealed interface CatalogUiEffect {
    data class Genres(val type: MediaType) : CatalogUiEffect
    data class Collection(val type: MediaType, val kind: CollectionKind) : CatalogUiEffect
    data class Details(val id: MediaId) : CatalogUiEffect
}
