package br.com.composesample.stream.model.collection

import br.com.composesample.stream.model.media.MediaId

sealed interface CollectionUiEffect {
    data class Details(val id: MediaId) : CollectionUiEffect
}
