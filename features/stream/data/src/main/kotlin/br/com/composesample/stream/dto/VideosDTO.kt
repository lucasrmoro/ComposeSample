package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class VideosDTO(@JsonName("results") val results: List<VideoDTO>? = null)
