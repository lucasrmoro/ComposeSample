package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class ReleaseDatesDTO(@JsonName("results") val results: List<RegionalReleaseDTO>? = null)
