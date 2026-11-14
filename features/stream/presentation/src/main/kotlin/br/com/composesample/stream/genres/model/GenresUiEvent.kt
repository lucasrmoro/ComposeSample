package br.com.composesample.stream.genres.model

import br.com.composesample.stream.model.catalog.Genre

sealed interface GenresUiEvent {
    data class Select(val genre: Genre) : GenresUiEvent
    data object Retry : GenresUiEvent
}
