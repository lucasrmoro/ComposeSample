package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class SeasonDTO(
    @JsonName("season_number") val number: Int = 0,
    @JsonName("name") val name: String = "",
    @JsonName("episode_count") val episodeCount: Int? = null,
    @JsonName("air_date") val airDate: String? = null,
    @JsonName("overview") val overview: String? = null,
    @JsonName("episodes") val episodes: List<EpisodeDTO>? = null
)
