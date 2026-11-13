package br.com.composesample.stream.genres.model

import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.media.MediaType

sealed interface GenresUiEvent {
    data class Start(val type: MediaType) : GenresUiEvent
    data class Select(val genre: Genre) : GenresUiEvent
    data object Retry : GenresUiEvent
}
