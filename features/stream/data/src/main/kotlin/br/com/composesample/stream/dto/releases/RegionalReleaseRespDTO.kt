package br.com.composesample.stream.dto.releases

import br.com.composesample.structural.network.http.JsonName

data class RegionalReleaseRespDTO(
    @JsonName("iso_3166_1") val country: String = "",
    @JsonName("release_dates") val dates: List<ReleaseRespDTO>? = null
)
