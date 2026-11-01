package br.com.composesample.structural.network.http

/**
 * Describes a path relative to the configured base URL. Absolute URLs are not accepted.
 * Placeholders such as `{id}` are replaced with individually encoded [pathParameters].
 * Query values and headers are unencoded; the HTTP implementation encodes query values.
 */
data class HttpRequest(
    val path: String,
    val pathParameters: Map<String, String> = emptyMap(),
    val query: Map<String, String> = emptyMap(),
    val headers: Map<String, String> = emptyMap()
)
