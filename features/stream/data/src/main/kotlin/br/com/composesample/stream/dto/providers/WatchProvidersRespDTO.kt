package br.com.composesample.stream.dto.providers

import br.com.composesample.structural.network.http.JsonName

data class WatchProvidersRespDTO(
    @JsonName("results") val results: Map<String, ProviderRegionRespDTO>? = null
)
