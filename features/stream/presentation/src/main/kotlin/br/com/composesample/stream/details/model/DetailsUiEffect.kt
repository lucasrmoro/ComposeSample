package br.com.composesample.stream.details.model

sealed interface DetailsUiEffect {

    data object None : DetailsUiEffect

}
