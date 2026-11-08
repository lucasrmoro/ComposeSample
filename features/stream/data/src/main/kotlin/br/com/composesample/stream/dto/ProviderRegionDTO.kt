package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class ProviderRegionDTO(
    @JsonName("link") val link: String? = null,
    @JsonName("flatrate") val subscription: List<ProviderDTO>? = null,
    @JsonName("free") val free: List<ProviderDTO>? = null,
    @JsonName("ads") val ads: List<ProviderDTO>? = null,
    @JsonName("rent") val rent: List<ProviderDTO>? = null,
    @JsonName("buy") val buy: List<ProviderDTO>? = null
)
