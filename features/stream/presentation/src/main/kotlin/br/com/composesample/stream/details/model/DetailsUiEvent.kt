package br.com.composesample.stream.details.model

sealed interface DetailsUiEvent {
    data class SelectSeason(val number: Int) : DetailsUiEvent
    data object RetryDetails : DetailsUiEvent
    data object RetrySeason : DetailsUiEvent
}
