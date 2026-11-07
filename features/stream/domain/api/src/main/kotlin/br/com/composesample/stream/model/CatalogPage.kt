package br.com.composesample.stream.model

data class CatalogPage(
    val items: List<MediaSummary> = emptyList(),
    val hasNextPage: Boolean = false
)
