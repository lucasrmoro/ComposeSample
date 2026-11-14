package br.com.composesample.stream.details.model

import br.com.composesample.stream.model.details.MediaDetails
import br.com.composesample.stream.model.seasons.SeasonDetails
import br.com.composesample.structural.core.text.UiText

data class DetailsUiState(
    val details: MediaDetails? = null,
    val season: SeasonDetails? = null,
    val selectedSeason: Int? = null,
    val loading: Boolean = true,
    val seasonLoading: Boolean = false,
    val seasonError: UiText? = null,
    val online: Boolean = true,
    val error: UiText? = null
)
