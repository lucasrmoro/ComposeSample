package br.com.composesample.stream.model.collection

import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId

sealed interface CollectionUiEvent {
    data class OpenDetails(val id: MediaId) : CollectionUiEvent
    data object NextPage : CollectionUiEvent
    data object Retry : CollectionUiEvent
}
