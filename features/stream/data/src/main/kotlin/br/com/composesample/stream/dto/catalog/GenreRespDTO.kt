package br.com.composesample.stream.dto.catalog

import br.com.composesample.structural.network.http.JsonName

data class GenreRespDTO(
    @JsonName("id") val id: Int = 0,
    @JsonName("name") val name: String = ""
)
