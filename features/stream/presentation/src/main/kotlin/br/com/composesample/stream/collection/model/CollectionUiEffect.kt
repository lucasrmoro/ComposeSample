package br.com.composesample.stream.collection.model

import br.com.composesample.stream.model.media.MediaId

sealed interface CollectionUiEffect {
    data class Details(val id: MediaId) : CollectionUiEffect
}
