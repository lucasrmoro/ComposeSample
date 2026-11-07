package br.com.composesample.structural.network.http

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.Strictness
import java.lang.reflect.Field

/** Holds the shared JSON serializer without exposing vendor types to the DI module. */
class NetworkSerialization {

    internal val gson: Gson =
        GsonBuilder()
            .setStrictness(Strictness.STRICT)
            .setFieldNamingStrategy(::resolveJsonName)
            .create()

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
