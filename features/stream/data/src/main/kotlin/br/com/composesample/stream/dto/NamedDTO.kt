package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class NamedDTO(
    @JsonName("name") val name: String? = null,
    @JsonName("english_name") val englishName: String? = null
)
