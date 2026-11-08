package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class ContentRatingRespDTO(
    @JsonName("iso_3166_1") val country: String = "",
    @JsonName("rating") val rating: String? = null
)
