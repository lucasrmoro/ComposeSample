package br.com.composesample.stream.dto.releases

import br.com.composesample.structural.network.http.JsonName

data class ReleaseDatesRespDTO(
    @JsonName("results") val results: List<RegionalReleaseRespDTO>? = null
)
