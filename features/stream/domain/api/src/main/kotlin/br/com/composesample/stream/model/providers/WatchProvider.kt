package br.com.composesample.stream.model.providers

data class WatchProvider(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val category: ProviderCategory
)
