package br.com.composesample.structural.network.http

import java.lang.reflect.ParameterizedType

internal class GsonJsonCodec : JsonCodec {

    private val gson by lazy { HttpClientFactory().createGson() }

    override fun encode(value: Any): String = gson.toJson(value)

    override fun <T> decode(value: String, type: HttpResponseType<T>): T {
        val target =
            (type.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.single()
        return requireNotNull(gson.fromJson<T>(value, target))
    }

}

fun createJsonCodec(): JsonCodec = GsonJsonCodec()
