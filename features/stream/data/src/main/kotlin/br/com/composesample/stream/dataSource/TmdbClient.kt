package br.com.composesample.stream.dataSource

import br.com.composesample.structural.network.http.HttpClient
import br.com.composesample.structural.network.http.HttpRequest
import br.com.composesample.structural.network.http.get

internal class TmdbClient(private val httpClient: HttpClient, private val configured: Boolean) {

    suspend inline fun <reified T> get(
        path: String,
        query: Map<String, String>,
        pathParameters: Map<String, String> = emptyMap()
    ): T {
        check(configured) { "TMDB credential is not configured" }
        return httpClient.get(
            HttpRequest(path = path, query = query, pathParameters = pathParameters)
        )
    }

}
