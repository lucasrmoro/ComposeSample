package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class EventDTO(
    @JsonName("kind") val kind: String = "",
    @JsonName("date") val date: String = "",
    @JsonName("season") val season: Int? = null,
    @JsonName("episode") val episode: Int? = null
)
