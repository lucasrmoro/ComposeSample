package br.com.composesample.structural.network.http

import com.google.gson.Gson
import java.lang.reflect.ParameterizedType

internal class GsonJsonCodec(
    private val gson: Gson
) : JsonCodec {

    override fun encode(value: Any): String = gson.toJson(value)

    override fun <T> decode(value: String, type: HttpResponseType<T>): T {
        val target =
            (type.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.single()
        return requireNotNull(gson.fromJson<T>(value, target))
    }

}

fun createJsonCodec(serialization: NetworkSerialization): JsonCodec =
    GsonJsonCodec(serialization.gson)
