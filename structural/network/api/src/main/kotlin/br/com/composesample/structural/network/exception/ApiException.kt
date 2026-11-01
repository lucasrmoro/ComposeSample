package br.com.composesample.structural.network.exception

import br.com.composesample.structural.network.http.JsonName

data class ApiException(
    @JsonName("Status") val status: Int? = null,
    @JsonName("Message") val message: String? = null,
    @JsonName("ExceptionCode") val exceptionCode: Int? = null
)
