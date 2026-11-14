package br.com.composesample.stream.model.catalog

import br.com.composesample.stream.model.media.MediaSummary

data class CatalogPage(
    val items: List<MediaSummary> = emptyList(),
    val hasNextPage: Boolean = false
)
