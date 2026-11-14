package br.com.composesample.stream.dto.credits

import br.com.composesample.structural.network.http.JsonName

data class CreditsRespDTO(
    @JsonName("cast") val cast: List<PersonRespDTO>? = null,
    @JsonName("crew") val crew: List<PersonRespDTO>? = null
)
