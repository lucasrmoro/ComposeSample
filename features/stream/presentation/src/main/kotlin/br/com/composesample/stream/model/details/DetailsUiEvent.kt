package br.com.composesample.stream.model.details

sealed interface DetailsUiEvent {
    data class SelectSeason(val number: Int) : DetailsUiEvent
    data object RetryDetails : DetailsUiEvent
    data object RetrySeason : DetailsUiEvent
}
