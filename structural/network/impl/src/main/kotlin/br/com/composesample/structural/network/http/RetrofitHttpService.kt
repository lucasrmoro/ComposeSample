package br.com.composesample.structural.network.http

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

internal interface RetrofitHttpService {

    @Streaming
    @GET
    fun get(
        @Url path: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Call<ResponseBody>
    @Streaming
    @POST
    fun post(
        @Url path: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Call<ResponseBody>
    @Streaming
    @POST
    fun post(
        @Url path: String,
        @Body body: RequestBody,
        @QueryMap query: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Call<ResponseBody>
    @Streaming
    @PUT
    fun put(
        @Url path: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Call<ResponseBody>
    @Streaming
    @PUT
    fun put(
        @Url path: String,
        @Body body: RequestBody,
        @QueryMap query: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Call<ResponseBody>
    @Streaming
    @DELETE
    fun delete(
        @Url path: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Call<ResponseBody>
    @Streaming
    @HTTP(method = "DELETE", hasBody = true)
    fun delete(
        @Url path: String,
        @Body body: RequestBody,
        @QueryMap query: Map<String, String> = emptyMap(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Call<ResponseBody>

}
