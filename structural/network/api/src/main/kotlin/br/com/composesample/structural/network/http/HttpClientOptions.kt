package br.com.composesample.structural.network.http

/**
 * Configuration for an independent HTTP client; bearer credentials are never sent to another
 * origin.
 */
data class HttpClientOptions(
    val baseUrl: String,
    val headers: Map<String, String> = emptyMap(),
    val useAppLanguage: Boolean = false
)
