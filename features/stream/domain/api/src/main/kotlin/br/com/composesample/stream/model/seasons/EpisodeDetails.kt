package br.com.composesample.stream.model.seasons

data class EpisodeDetails(
    val number: Int,
    val name: String,
    val overview: String?,
    val airDate: String?,
    val runtime: Int?,
    val imagePath: String?
)
