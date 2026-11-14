package br.com.composesample.stream.genres.model

import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.structural.core.text.UiText

data class GenresUiState(
    val type: MediaType = MediaType.MOVIE,
    val genres: List<Genre> = emptyList(),
    val loading: Boolean = true,
    val error: UiText? = null
)
