package br.com.composesample.structural.network.http

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.Strictness
import java.lang.reflect.Field

/** Owns shared network components without exposing implementation or vendor types to DI. */
class NetworkComponents {

    internal val gson: Gson =
        GsonBuilder()
            .setStrictness(Strictness.STRICT)
            .setFieldNamingStrategy(::resolveJsonName)
            .create()

    internal val httpClientFactory: HttpClientFactory by lazy { HttpClientFactory(gson) }

    private fun resolveJsonName(field: Field): String {
        val name = field.getAnnotation(JsonName::class.java)?.value
        if (name.isNullOrBlank()) {
            throw JsonParseException(
                "Field ${field.declaringClass.name}.${field.name} requires @JsonName"
            )
        }
        return name
    }

}
