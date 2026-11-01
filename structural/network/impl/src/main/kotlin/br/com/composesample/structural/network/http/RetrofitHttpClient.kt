package br.com.composesample.structural.network.http

import br.com.composesample.structural.network.exception.ApiException
import br.com.composesample.structural.network.exception.HttpException
import br.com.composesample.structural.network.exception.HttpSerializationException
import com.google.gson.Gson
import com.google.gson.JsonParseException
import java.lang.reflect.ParameterizedType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

internal class RetrofitHttpClient(
    private val service: RetrofitHttpService,
    private val gson: Gson,
    private val dispatcher: CoroutineDispatcher
) : HttpClient {

    override suspend fun <T> get(request: HttpRequest, responseType: HttpResponseType<T>): T =
        execute(responseType) { service.get(request.resolvePath(), request.query, request.headers) }

    override suspend fun <T> post(
        request: HttpRequest,
        body: Any?,
        responseType: HttpResponseType<T>
    ): T = execute(responseType) {
        service.post(
            request.resolvePath(),
            request.query,
            request.headers,
            encode(body)
        )
    }

    override suspend fun <T> put(
        request: HttpRequest,
        body: Any?,
        responseType: HttpResponseType<T>
    ): T = execute(responseType) {
        service.put(
            request.resolvePath(),
            request.query,
            request.headers,
            encode(body)
        )
    }

    override suspend fun <T> delete(
        request: HttpRequest,
        body: Any?,
        responseType: HttpResponseType<T>
    ): T = execute(responseType) {
        service.delete(
            request.resolvePath(),
            request.query,
            request.headers,
            encode(body)
        )
    }

    private suspend fun <T> execute(
        responseType: HttpResponseType<T>,
        call: () -> Call<ResponseBody>
    ): T = withContext(dispatcher) {
        call().awaitDecoded { response ->
            if (!response.isSuccessful) throw response.toHttpException()
            decode(response.body(), responseType)
        }
    }

    private fun encode(body: Any?) = try {
        gson.toJson(body).toRequestBody("application/json; charset=utf-8".toMediaType())
    } catch (exception: JsonParseException) {
        throw HttpSerializationException(exception)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> decode(body: ResponseBody?, responseType: HttpResponseType<T>): T {
        val superclass = responseType.javaClass.genericSuperclass as? ParameterizedType
        val type = requireNotNull(superclass?.actualTypeArguments?.singleOrNull()) {
            "Use a direct HttpResponseType subclass with concrete type arguments"
        }
        if (type == Unit::class.java) return Unit as T
        if (body == null) {
            throw HttpSerializationException(IllegalStateException("Expected a JSON response body"))
        }
        try {
            return gson.fromJson<T>(body.charStream(), type)
                ?: throw HttpSerializationException(IllegalStateException("Expected a JSON response body"))
        } catch (exception: JsonParseException) {
            throw HttpSerializationException(exception)
        }
    }

    private fun Response<ResponseBody>.toHttpException(): HttpException {
        val error = errorBody()?.let { body ->
            try {
                gson.fromJson(body.charStream(), ApiException::class.java)
            } catch (_: JsonParseException) {
                null
            }
        } ?: ApiException()
        return HttpException(statusCode = code(), apiException = error, message = message())
    }

}
