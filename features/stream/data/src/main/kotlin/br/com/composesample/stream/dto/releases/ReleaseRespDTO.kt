package br.com.composesample.stream.dto.releases

import br.com.composesample.structural.network.http.JsonName

data class ReleaseRespDTO(
    @JsonName("certification") val certification: String? = null,
    @JsonName("release_date") val date: String? = null
)
