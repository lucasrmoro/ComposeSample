package br.com.composesample.structural.media_compression.impl

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.DefaultEncoderFactory
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.VideoEncoderSettings
import br.com.composesample.structural.media.MediaFile
import br.com.composesample.structural.media_compression.api.CompressionFailure
import br.com.composesample.structural.media_compression.api.CompressionPreset
import br.com.composesample.structural.media_compression.api.CompressionResult
import br.com.composesample.structural.media_compression.api.VideoCompressionOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

internal class VideoMediaCompressor(
    context: Context,
    private val outputStore: CompressedMediaStore,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val context = context.applicationContext

    suspend fun compress(
        media: MediaFile,
        options: VideoCompressionOptions
    ): CompressionResult = withContext(ioDispatcher) {
        if (!media.mimeType.startsWith(MediaCompressionConstants.VIDEO_MIME_PREFIX)) {
            return@withContext CompressionResult.Failed(CompressionFailure.UNSUPPORTED_FORMAT)
        }

        val inputFile = createTemporaryFile(
            MediaCompressionConstants.VIDEO_INPUT_TEMP_FILE_PREFIX,
            ".${media.extension}"
        )
            ?: return@withContext CompressionResult.Failed(CompressionFailure.PROCESSING_FAILED)
        val compressedFile = createTemporaryFile(
            MediaCompressionConstants.VIDEO_OUTPUT_TEMP_FILE_PREFIX,
            ".${MediaCompressionConstants.MP4_EXTENSION}"
        )
            ?: run {
                inputFile.delete()
                return@withContext CompressionResult.Failed(CompressionFailure.PROCESSING_FAILED)
            }
        try {
            media.openStream().use { input -> inputFile.outputStream().use(input::copyTo) }
            transcode(inputFile, compressedFile, options)
            val originalSize = media.sizeBytes
            if (compressedFile.length() >= originalSize) {
                return@withContext CompressionResult.Failed(CompressionFailure.NO_SIZE_REDUCTION)
            }
            saveCompressedVideo(media, compressedFile, originalSize, options)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            CompressionResult.Failed(CompressionFailure.PROCESSING_FAILED)
        } finally {
            inputFile.delete()
            compressedFile.delete()
        }
    }

    private fun createTemporaryFile(prefix: String, suffix: String): File? = runCatching {
        File.createTempFile(prefix, suffix, context.cacheDir)
    }.getOrNull()

    private fun saveCompressedVideo(
        source: MediaFile,
        compressedFile: File,
        originalSize: Long,
        options: VideoCompressionOptions
    ): CompressionResult = try {
        val result = outputStore.save(
            source = source,
            compressedFile = compressedFile,
            outputMode = options.outputMode,
            outputNameWithoutExtension = options.outputNameWithoutExtension,
            extension = MediaCompressionConstants.MP4_EXTENSION,
            mimeType = MediaCompressionConstants.MP4_MIME_TYPE
        )
        CompressionResult.Compressed(result, originalSize, result.sizeBytes)
    } catch (_: Exception) {
        CompressionResult.Failed(CompressionFailure.STORAGE_FAILED)
    }

    private suspend fun transcode(
        inputFile: File,
        outputFile: File,
        options: VideoCompressionOptions
    ) = suspendCancellableCoroutine { continuation ->
        Handler(Looper.getMainLooper()).post {
            if (continuation.isActive) {
                startTranscode(inputFile, outputFile, options, continuation)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun startTranscode(
        inputFile: File,
        outputFile: File,
        options: VideoCompressionOptions,
        continuation: CancellableContinuation<Unit>
    ) {
        try {
            val transformer = createTransformer(inputFile, options, continuation)
            continuation.invokeOnCancellation {
                Handler(Looper.getMainLooper()).post { transformer.cancel() }
            }
            transformer.start(
                EditedMediaItem.Builder(MediaItem.fromUri(Uri.fromFile(inputFile))).build(),
                outputFile.absolutePath
            )
        } catch (exception: Exception) {
            if (continuation.isActive) continuation.resumeWith(Result.failure(exception))
        }
    }

    @OptIn(UnstableApi::class)
    private fun createTransformer(
        inputFile: File,
        options: VideoCompressionOptions,
        continuation: CancellableContinuation<Unit>
    ): Transformer {
        val bitrate = options.targetBitrate ?: suggestedBitrate(inputFile, options.preset)
        val encoderFactory = createEncoderFactory(bitrate)
        return Transformer.Builder(context)
            .setEncoderFactory(encoderFactory)
            .addListener(createListener(continuation))
            .build()
    }

    @OptIn(UnstableApi::class)
    private fun createEncoderFactory(bitrate: Int?): DefaultEncoderFactory =
        DefaultEncoderFactory.Builder(context)
            .setEnableFallback(false)
            .apply {
                if (bitrate != null) {
                    setRequestedVideoEncoderSettings(
                        VideoEncoderSettings.Builder().setBitrate(bitrate).build()
                    )
                }
            }
            .build()

    @OptIn(UnstableApi::class)
    private fun createListener(
        continuation: CancellableContinuation<Unit>
    ): Transformer.Listener = object : Transformer.Listener {

        override fun onCompleted(composition: Composition, exportResult: ExportResult) {
            if (continuation.isActive) continuation.resume(Unit)
        }

        override fun onError(
            composition: Composition,
            exportResult: ExportResult,
            exportException: ExportException
        ) {
            if (continuation.isActive) continuation.resumeWith(Result.failure(exportException))
        }

    }

    private fun suggestedBitrate(inputFile: File, preset: CompressionPreset): Int? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(inputFile.absolutePath)
            val sourceBitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                ?.toIntOrNull() ?: return null
            (sourceBitrate * preset.videoBitrateRatio).toInt()
                .coerceAtLeast(MediaCompressionConstants.MINIMUM_VIDEO_BITRATE_BPS)
        } catch (_: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

}
