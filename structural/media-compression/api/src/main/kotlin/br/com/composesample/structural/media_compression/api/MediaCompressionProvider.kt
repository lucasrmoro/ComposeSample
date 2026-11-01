package br.com.composesample.structural.media_compression.api

import br.com.composesample.structural.media.MediaFile

/** Compresses media only when the caller explicitly requests it. */
interface MediaCompressionProvider {

    /** Compresses an image while preserving its orientation metadata. */
    suspend fun compressImage(
        media: MediaFile,
        options: ImageCompressionOptions = ImageCompressionOptions()
    ): CompressionResult

    /** Transcodes a video without changing its dimensions or frame rate. */
    suspend fun compressVideo(
        media: MediaFile,
        options: VideoCompressionOptions = VideoCompressionOptions()
    ): CompressionResult

}
