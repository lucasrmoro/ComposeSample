package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class ContentRatingsDTO(@JsonName("results") val results: List<ContentRatingDTO>? = null)
