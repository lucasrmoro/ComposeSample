package br.com.composesample.stream.dataSource

import br.com.composesample.structural.network.http.HttpClient

internal class TmdbClient(val httpClient: HttpClient, val configured: Boolean)
