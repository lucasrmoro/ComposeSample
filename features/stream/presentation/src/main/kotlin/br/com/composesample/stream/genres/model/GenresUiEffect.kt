package br.com.composesample.stream.genres.model

import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.media.MediaType

sealed interface GenresUiEffect {
    data class Collection(val type: MediaType, val genre: Genre) : GenresUiEffect
}
