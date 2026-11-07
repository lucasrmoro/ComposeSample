package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class RegionalReleaseDTO(
    @JsonName("iso_3166_1") val country: String = "",
    @JsonName("release_dates") val dates: List<ReleaseDTO>? = null
)
