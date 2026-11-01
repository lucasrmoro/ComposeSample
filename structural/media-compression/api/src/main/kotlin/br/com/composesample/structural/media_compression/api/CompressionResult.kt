package br.com.composesample.structural.media_compression.api

import br.com.composesample.structural.media.MediaFile

/** Returns the resulting media reference or a typed compression failure. */
sealed interface CompressionResult {

    /** Compression produced a smaller media item. */
    data class Compressed(
        val media: MediaFile,
        val originalSizeBytes: Long,
        val compressedSizeBytes: Long
    ) : CompressionResult

    /** Compression did not produce a usable output. */
    data class Failed(val reason: CompressionFailure) : CompressionResult

}
