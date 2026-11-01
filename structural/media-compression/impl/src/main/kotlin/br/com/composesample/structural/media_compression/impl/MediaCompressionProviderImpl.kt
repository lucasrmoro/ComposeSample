package br.com.composesample.structural.media_compression.impl

import android.content.Context
import br.com.composesample.structural.media.MediaFile
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.media_compression.api.CompressionResult
import br.com.composesample.structural.media_compression.api.ImageCompressionOptions
import br.com.composesample.structural.media_compression.api.MediaCompressionProvider
import br.com.composesample.structural.media_compression.api.VideoCompressionOptions
import kotlinx.coroutines.CoroutineDispatcher

internal class MediaCompressionProviderImpl(
    context: Context,
    storageProvider: MediaStorageProvider,
    ioDispatcher: CoroutineDispatcher
) : MediaCompressionProvider {

    private val imageCompressor = ImageMediaCompressor(
        context = context,
        outputStore = CompressedMediaStore(storageProvider),
        ioDispatcher = ioDispatcher
    )
    private val videoCompressor = VideoMediaCompressor(
        context = context,
        outputStore = CompressedMediaStore(storageProvider),
        ioDispatcher = ioDispatcher
    )

    override suspend fun compressImage(
        media: MediaFile,
        options: ImageCompressionOptions
    ): CompressionResult = imageCompressor.compress(media, options)

    override suspend fun compressVideo(
        media: MediaFile,
        options: VideoCompressionOptions
    ): CompressionResult = videoCompressor.compress(media, options)

}
