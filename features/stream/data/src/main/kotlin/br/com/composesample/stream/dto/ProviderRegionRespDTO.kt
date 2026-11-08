package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class ProviderRegionRespDTO(
    @JsonName("link") val link: String? = null,
    @JsonName("flatrate") val subscription: List<ProviderRespDTO>? = null,
    @JsonName("free") val free: List<ProviderRespDTO>? = null,
    @JsonName("ads") val ads: List<ProviderRespDTO>? = null,
    @JsonName("rent") val rent: List<ProviderRespDTO>? = null,
    @JsonName("buy") val buy: List<ProviderRespDTO>? = null
)
