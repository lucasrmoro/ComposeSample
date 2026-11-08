package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class CreditsDTO(
    @JsonName("cast") val cast: List<PersonDTO>? = null,
    @JsonName("crew") val crew: List<PersonDTO>? = null
)
