package br.com.composesample.stream.catalog.model

import br.com.composesample.stream.navigation.StreamDestination

sealed interface CatalogUiEffect {
    data class Navigate(val destination: StreamDestination) : CatalogUiEffect
}
