package br.com.composesample.structural.network.http

import android.webkit.URLUtil
import java.io.IOException
import java.io.OutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

internal class BinaryDownloadProviderImpl(private val coroutineDispatcher: CoroutineDispatcher) :
    BinaryDownloadProvider {

    private val okHttpClient by lazy {
        OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).build()
    }

    override fun download(
        url: String,
        output: OutputStream,
        maximumBytes: Long
    ) = flow {
        val target = url.toHttpUrl()
        require(target.isHttps) {
            UNSUPPORTED_ORIGIN_MESSAGE
        }
        require(target.username.isEmpty() && target.password.isEmpty() && maximumBytes > 0)
        val result =
            suspendCancellableCoroutine<BinaryDownloadResult> { continuation ->
                val call = okHttpClient.newCall(Request.Builder().url(target).build())
                continuation.invokeOnCancellation { call.cancel() }
                call.enqueue(
                    object : Callback {

                        override fun onFailure(call: Call, exception: IOException) {
                            if (continuation.isActive) continuation.resumeWithException(exception)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            try {
                                val result = response.use {
                                    if (!it.isSuccessful)
                                        throw IOException("$DOWNLOAD_FAILED_MESSAGE${it.code}")
                                    val body = requireNotNull(it.body)
                                    val mimeType =
                                        body.contentType()?.let { type ->
                                            "${type.type}/${type.subtype}"
                                        } ?: BINARY_MIME_TYPE
                                    val fileName =
                                        URLUtil.guessFileName(
                                            it.request.url.toString(),
                                            it.header(CONTENT_DISPOSITION_HEADER),
                                            mimeType
                                        )
                                    val extension =
                                        fileName
                                            .substringAfterLast(
                                                EXTENSION_SEPARATOR,
                                                missingDelimiterValue = ""
                                            )
                                            .takeIf { value -> value.matches(EXTENSION_PATTERN) }
                                            ?: BINARY_EXTENSION
                                    val bytes =
                                        body.byteStream().use { input ->
                                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                                            var bytesWritten = 0L
                                            while (true) {
                                                if (!continuation.isActive)
                                                    throw IOException(CANCELLED_MESSAGE)
                                                val read = input.read(buffer)
                                                if (read < 0) break
                                                bytesWritten += read
                                                if (bytesWritten > maximumBytes)
                                                    throw IOException(SIZE_LIMIT_MESSAGE)
                                                output.write(buffer, 0, read)
                                            }
                                            bytesWritten
                                        }
                                    BinaryDownloadResult(
                                        bytesWritten = bytes,
                                        mimeType = mimeType,
                                        extension = extension
                                    )
                                }
                                if (continuation.isActive) continuation.resume(result)
                            } catch (exception: Exception) {
                                if (continuation.isActive)
                                    continuation.resumeWithException(exception)
                            }
                        }

                    }
                )
            }
        emit(result)
    }
        .flowOn(coroutineDispatcher)

    private companion object {
        const val DOWNLOAD_FAILED_MESSAGE = "Download failed: "
        const val UNSUPPORTED_ORIGIN_MESSAGE = "Unsupported download origin"
        const val CANCELLED_MESSAGE = "Download cancelled"
        const val SIZE_LIMIT_MESSAGE = "Download exceeds byte limit"
        const val CONTENT_DISPOSITION_HEADER = "Content-Disposition"
        const val BINARY_MIME_TYPE = "application/octet-stream"
        const val BINARY_EXTENSION = "bin"
        const val EXTENSION_SEPARATOR = '.'
        val EXTENSION_PATTERN = Regex("[A-Za-z0-9]{1,16}")
    }
}

fun createBinaryDownloadProvider(coroutineDispatcher: CoroutineDispatcher): BinaryDownloadProvider =
    BinaryDownloadProviderImpl(coroutineDispatcher)
