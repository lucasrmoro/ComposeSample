package br.com.composesample.stream.model

data class MediaSummary(
    val id: MediaId,
    val title: String,
    val posterPath: String?,
    val year: String?,
    val rating: Double?,
    val certification: String?,
    val releaseEvent: ReleaseEvent?
)
