package br.com.composesample.structural.network.http

import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

/**
 * Streams a binary response from an approved HTTPS origin into caller-owned output. Preserves
 * response type information and never attaches application credentials.
 */
interface BinaryDownloadProvider {

    /**
     * Downloads once per collection without closing [output]. Failures may leave partial bytes
     * in the stream; callers must discard incomplete files. Only [allowedHosts] are accepted,
     * redirects are rejected, and content exceeding [maximumBytes] fails. Cancellation aborts
     * the underlying request.
     */
    fun download(
        url: String,
        output: OutputStream,
        maximumBytes: Long,
        allowedHosts: Set<String>
    ): Flow<BinaryDownloadResult>

}
