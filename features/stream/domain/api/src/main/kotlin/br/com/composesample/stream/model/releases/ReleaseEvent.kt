package br.com.composesample.stream.model.releases

data class ReleaseEvent(
    val kind: ReleaseKind,
    val date: String,
    val season: Int?,
    val episode: Int?
)
