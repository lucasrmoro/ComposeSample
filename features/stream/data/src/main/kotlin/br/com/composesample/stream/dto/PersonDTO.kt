package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class PersonDTO(
    @JsonName("name") val name: String = "",
    @JsonName("character") val character: String? = null,
    @JsonName("job") val job: String? = null,
    @JsonName("profile_path") val profilePath: String? = null
)
