package br.com.composesample.stream.model.catalog

import br.com.composesample.stream.navigation.StreamDestination

sealed interface CatalogUiEffect {
    data class Navigate(val destination: StreamDestination) : CatalogUiEffect
}
