package br.com.composesample.stream.dataSource

import br.com.composesample.structural.network.http.HttpClient
import java.time.Clock
import kotlinx.coroutines.CoroutineDispatcher

fun createStreamRemoteDataSource(
    httpClient: HttpClient,
    coroutineDispatcher: CoroutineDispatcher,
    clock: Clock
): StreamRemoteDataSource =
    StreamRemoteDataSourceImpl(
        httpClient = httpClient,
        coroutineDispatcher = coroutineDispatcher,
        clock = clock
    )
