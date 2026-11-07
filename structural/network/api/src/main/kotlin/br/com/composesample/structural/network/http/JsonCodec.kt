package br.com.composesample.structural.network.http

/** Encodes and decodes annotated HTTP-shaped objects without exposing a serialization SDK. */
interface JsonCodec {

    fun encode(value: Any): String
    fun <T> decode(value: String, type: HttpResponseType<T>): T

}

inline fun <reified T> JsonCodec.decode(value: String): T =
    decode(value, object : HttpResponseType<T>() {})
