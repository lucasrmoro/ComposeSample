package br.com.composesample.stream.collection.model

import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId

sealed interface CollectionUiEvent {
    data class OpenDetails(val id: MediaId) : CollectionUiEvent
    data object NextPage : CollectionUiEvent
    data object Retry : CollectionUiEvent
}
