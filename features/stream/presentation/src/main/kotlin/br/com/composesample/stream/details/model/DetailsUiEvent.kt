package br.com.composesample.stream.details.model

import br.com.composesample.stream.model.media.MediaId


sealed interface DetailsUiEvent {

    data class Start(val id: MediaId) : DetailsUiEvent

    data class SelectSeason(val number: Int) : DetailsUiEvent

    data object Retry : DetailsUiEvent

}
