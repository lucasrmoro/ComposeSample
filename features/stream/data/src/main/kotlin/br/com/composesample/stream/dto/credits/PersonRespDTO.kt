package br.com.composesample.stream.dto.credits

import br.com.composesample.structural.network.http.JsonName

data class PersonRespDTO(
    @JsonName("name") val name: String = "",
    @JsonName("character") val character: String? = null,
    @JsonName("job") val job: String? = null,
    @JsonName("profile_path") val profilePath: String? = null
)
