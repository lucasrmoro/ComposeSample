package br.com.composesample.stream.model.media

data class MediaSummary(
    val id: MediaId,
    val title: String,
    val posterPath: String?,
    val year: String?,
    val rating: Double?,
    val certification: String?
)
