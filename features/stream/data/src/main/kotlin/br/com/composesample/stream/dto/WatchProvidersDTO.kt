package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class WatchProvidersDTO(
    @JsonName("results") val results: Map<String, ProviderRegionDTO>? = null
)
