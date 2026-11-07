package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class VideoDTO(
    @JsonName("key") val key: String = "",
    @JsonName("site") val site: String = "",
    @JsonName("name") val name: String = "",
    @JsonName("type") val type: String? = null,
    @JsonName("official") val official: Boolean = false
)
