package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class ReleaseDTO(
    @JsonName("certification") val certification: String? = null,
    @JsonName("release_date") val date: String? = null
)
