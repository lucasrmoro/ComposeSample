package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class WatchProvidersDTO(
    @JsonName("results") val results: Map<String, ProviderRegionDTO>? = null
)
