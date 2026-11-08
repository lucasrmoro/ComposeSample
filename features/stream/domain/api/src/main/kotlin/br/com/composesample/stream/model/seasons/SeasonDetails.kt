package br.com.composesample.stream.model.seasons

data class SeasonDetails(
    val number: Int,
    val name: String,
    val overview: String?,
    val episodes: List<EpisodeDetails>
)
