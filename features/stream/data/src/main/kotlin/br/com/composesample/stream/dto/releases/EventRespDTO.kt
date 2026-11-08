package br.com.composesample.stream.dto.releases

import br.com.composesample.structural.network.http.JsonName

data class EventRespDTO(
    @JsonName("kind") val kind: String = "",
    @JsonName("date") val date: String = "",
    @JsonName("season") val season: Int? = null,
    @JsonName("episode") val episode: Int? = null
)
