package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class ContentRatingsRespDTO(
    @JsonName("results") val results: List<ContentRatingRespDTO>? = null
)
