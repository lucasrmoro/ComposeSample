package br.com.composesample.stream.model.details

sealed interface DetailsUiEffect {
    data object None : DetailsUiEffect
}
