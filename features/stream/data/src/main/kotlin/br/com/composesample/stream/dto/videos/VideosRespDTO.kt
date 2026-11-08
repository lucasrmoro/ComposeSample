package br.com.composesample.stream.dto.videos

import br.com.composesample.structural.network.http.JsonName

data class VideosRespDTO(@JsonName("results") val results: List<VideoRespDTO>? = null)
