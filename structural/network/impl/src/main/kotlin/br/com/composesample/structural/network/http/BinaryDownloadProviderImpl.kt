package br.com.composesample.structural.network.http

import android.webkit.URLUtil
import java.io.IOException
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody

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
        require(target.isHttps) { UNSUPPORTED_ORIGIN_MESSAGE }
        require(target.username.isEmpty() && target.password.isEmpty() && maximumBytes > 0)
        val context = currentCoroutineContext()
        val service = HttpClientFactory().createService(
            baseUrl = requireNotNull(target.resolve("/")).toString(),
            client = okHttpClient
        )
        val result = service.get(target.toString(), emptyMap(), emptyMap()).awaitDecoded { response ->
            if (!response.isSuccessful) throw IOException("$DOWNLOAD_FAILED_MESSAGE${response.code()}")
            val body = requireNotNull(response.body())
            val mimeType = body.contentType()?.let { "${it.type}/${it.subtype}" } ?: BINARY_MIME_TYPE
            BinaryDownloadResult(
                bytesWritten = body.writeTo(output, maximumBytes, context),
                mimeType = mimeType,
                extension = resolveExtension(url, response.headers()[CONTENT_DISPOSITION_HEADER], mimeType)
            )
        }
        emit(result)
    }.flowOn(coroutineDispatcher)

    private fun ResponseBody.writeTo(
        output: OutputStream,
        maximumBytes: Long,
        context: CoroutineContext
    ): Long = byteStream().use { input ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytesWritten = 0L
        while (true) {
            context.ensureActive()
            val read = input.read(buffer)
            if (read < 0) break
            if (read.toLong() > maximumBytes - bytesWritten) throw IOException(SIZE_LIMIT_MESSAGE)
            output.write(buffer, 0, read)
            bytesWritten += read
        }
        bytesWritten
    }

    private fun resolveExtension(url: String, contentDisposition: String?, mimeType: String): String =
        URLUtil.guessFileName(url, contentDisposition, mimeType)
            .substringAfterLast(EXTENSION_SEPARATOR, missingDelimiterValue = "")
            .takeIf { it.matches(EXTENSION_PATTERN) }
            ?: BINARY_EXTENSION

    private companion object {
        const val DOWNLOAD_FAILED_MESSAGE = "Download failed: "
        const val UNSUPPORTED_ORIGIN_MESSAGE = "Unsupported download origin"
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
