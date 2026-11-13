package br.com.composesample.stream.collection.model

import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType


sealed interface CollectionUiEvent {

    data class Start(val type: MediaType, val kind: CollectionKind, val genreId: Int?) :
        CollectionUiEvent

    data class OpenDetails(val id: MediaId) : CollectionUiEvent

    data object NextPage : CollectionUiEvent

    data object Retry : CollectionUiEvent

}
