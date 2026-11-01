package br.com.composesample.structural.network.http

/**
 * Executes JSON requests without exposing a transport or serialization library.
 * Non-success responses throw HttpException; invalid JSON throws HttpSerializationException.
 * Transport I/O errors and coroutine cancellation propagate to the caller.
 * Request Unit for endpoints with no response body. Every DTO field must declare [JsonName].
 */
interface HttpClient {

    suspend fun <T> get(request: HttpRequest, responseType: HttpResponseType<T>): T

    /** Serializes [body] as JSON; a null body is sent as JSON null. */
    suspend fun <T> post(request: HttpRequest, body: Any?, responseType: HttpResponseType<T>): T

    suspend fun <T> put(request: HttpRequest, body: Any?, responseType: HttpResponseType<T>): T

    suspend fun <T> delete(request: HttpRequest, body: Any?, responseType: HttpResponseType<T>): T

}
