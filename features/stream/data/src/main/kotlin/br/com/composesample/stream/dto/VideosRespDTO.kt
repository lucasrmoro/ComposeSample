package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class VideosRespDTO(@JsonName("results") val results: List<VideoRespDTO>? = null)
