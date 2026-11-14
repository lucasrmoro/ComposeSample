package br.com.composesample.stream.model.seasons

data class SeasonSummary(
    val number: Int,
    val name: String,
    val episodeCount: Int?,
    val airDate: String?
)
