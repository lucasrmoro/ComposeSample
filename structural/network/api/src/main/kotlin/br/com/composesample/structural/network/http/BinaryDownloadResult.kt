package br.com.composesample.structural.network.http

/** Response metadata for a streamed download; no content bytes are retained in this model. */
data class BinaryDownloadResult(val bytesWritten: Long, val mimeType: String, val extension: String)
