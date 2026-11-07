package br.com.composesample.structural.network.http

/** Creates independent clients without changing the application login client. */
interface ConfiguredHttpClientProvider {

    fun create(options: HttpClientOptions): HttpClient

}
