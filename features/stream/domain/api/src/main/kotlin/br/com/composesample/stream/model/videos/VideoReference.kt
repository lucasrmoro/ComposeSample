package br.com.composesample.stream.model.videos

data class VideoReference(
    val key: String,
    val site: String,
    val name: String,
    val type: String?,
    val official: Boolean
)
