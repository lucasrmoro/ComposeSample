package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class EpisodeDTO(
    @JsonName("episode_number") val number: Int = 0,
    @JsonName("season_number") val season: Int = 0,
    @JsonName("name") val name: String = "",
    @JsonName("overview") val overview: String? = null,
    @JsonName("air_date") val airDate: String? = null,
    @JsonName("runtime") val runtime: Int? = null,
    @JsonName("still_path") val stillPath: String? = null
)
