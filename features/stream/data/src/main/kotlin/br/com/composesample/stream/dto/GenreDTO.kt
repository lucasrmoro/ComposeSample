package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class GenreDTO(
    @JsonName("id") val id: Int = 0,
    @JsonName("name") val name: String = ""
)
