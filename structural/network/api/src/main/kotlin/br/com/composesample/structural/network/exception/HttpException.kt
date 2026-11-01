package br.com.composesample.structural.network.exception

/** An HTTP failure whose optional server error payload has already been decoded. */
class HttpException(
    val statusCode: Int,
    val apiException: ApiException = ApiException(),
    override val message: String = "HTTP $statusCode"
) : Exception(message)
