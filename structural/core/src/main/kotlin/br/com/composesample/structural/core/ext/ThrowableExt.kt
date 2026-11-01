package br.com.composesample.structural.core.ext

import br.com.composesample.structural.network.exception.ApiException
import br.com.composesample.structural.network.exception.HttpException
import br.com.composesample.structural.network.exception.SampleException

internal fun Throwable?.toSampleException() = SampleException(
    message = this?.message.orEmpty(),
    apiException = (this as? HttpException)?.apiException ?: ApiException()
)
