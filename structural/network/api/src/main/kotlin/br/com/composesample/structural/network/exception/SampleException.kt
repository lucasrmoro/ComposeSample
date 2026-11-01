package br.com.composesample.structural.network.exception

class SampleException(
    override val message: String?,
    val apiException: ApiException = ApiException()
) : Throwable()