package br.com.composesample.stream.model

enum class ReleaseKind {
    NEW_SERIES,
    NEW_SEASON,
    NEW_EPISODE
}

data class ReleaseEvent(
    val kind: ReleaseKind,
    val date: String,
    val season: Int?,
    val episode: Int?
)
