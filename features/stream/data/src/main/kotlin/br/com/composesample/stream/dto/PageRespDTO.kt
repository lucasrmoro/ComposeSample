package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class PageRespDTO(
    @JsonName("page") val page: Int = 1,
    @JsonName("total_pages") val totalPages: Int = 1,
    @JsonName("results") val results: List<MediaRespDTO> = emptyList()
)
