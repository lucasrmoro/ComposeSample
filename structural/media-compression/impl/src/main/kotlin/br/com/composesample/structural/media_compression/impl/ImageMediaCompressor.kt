package br.com.composesample.structural.media_compression.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import br.com.composesample.structural.media.MediaFile
import br.com.composesample.structural.media_compression.api.CompressionFailure
import br.com.composesample.structural.media_compression.api.CompressionResult
import br.com.composesample.structural.media_compression.api.ImageCompressionOptions
import br.com.composesample.structural.media_compression.api.LocationMetadataPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

internal class ImageMediaCompressor(
    context: Context,
    private val outputStore: CompressedMediaStore,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val cacheDirectory = context.applicationContext.cacheDir

    suspend fun compress(
        media: MediaFile,
        options: ImageCompressionOptions
    ): CompressionResult = withContext(ioDispatcher) {
        if (!media.mimeType.startsWith(MediaCompressionConstants.IMAGE_MIME_PREFIX)) {
            return@withContext CompressionResult.Failed(CompressionFailure.UNSUPPORTED_FORMAT)
        }

        val compressedFile = createTemporaryImage()
            ?: return@withContext CompressionResult.Failed(CompressionFailure.PROCESSING_FAILED)
        try {
            val originalSize = media.sizeBytes
            val bitmap = decodeImage(media)
                ?: return@withContext CompressionResult.Failed(CompressionFailure.INVALID_MEDIA)
            if (!encodeImage(bitmap, compressedFile, options.preset.imageQuality)) {
                return@withContext CompressionResult.Failed(CompressionFailure.PROCESSING_FAILED)
            }
            copyExif(media, compressedFile, options.locationMetadataPolicy)
            if (compressedFile.length() >= originalSize) {
                return@withContext CompressionResult.Failed(CompressionFailure.NO_SIZE_REDUCTION)
            }
            saveCompressedImage(media, compressedFile, originalSize, options)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            CompressionResult.Failed(CompressionFailure.PROCESSING_FAILED)
        } finally {
            compressedFile.delete()
        }
    }

    private fun createTemporaryImage(): File? = runCatching {
        File.createTempFile(
            MediaCompressionConstants.IMAGE_TEMP_FILE_PREFIX,
            ".${MediaCompressionConstants.JPEG_EXTENSION}",
            cacheDirectory
        )
    }.getOrNull()

    private fun decodeImage(media: MediaFile): Bitmap? =
        media.openStream().use(BitmapFactory::decodeStream)

    private fun encodeImage(bitmap: Bitmap, destination: File, quality: Int): Boolean = try {
        destination.outputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }
    } finally {
        bitmap.recycle()
    }

    private fun saveCompressedImage(
        source: MediaFile,
        compressedFile: File,
        originalSize: Long,
        options: ImageCompressionOptions
    ): CompressionResult = try {
        val result = outputStore.save(
            source = source,
            compressedFile = compressedFile,
            outputMode = options.outputMode,
            outputNameWithoutExtension = options.outputNameWithoutExtension,
            extension = MediaCompressionConstants.JPEG_EXTENSION,
            mimeType = MediaCompressionConstants.JPEG_MIME_TYPE
        )
        CompressionResult.Compressed(result, originalSize, result.sizeBytes)
    } catch (_: Exception) {
        CompressionResult.Failed(CompressionFailure.STORAGE_FAILED)
    }

    private fun copyExif(
        source: MediaFile,
        destination: File,
        locationPolicy: LocationMetadataPolicy
    ) {
        if (!source.mimeType.equals(MediaCompressionConstants.JPEG_MIME_TYPE, ignoreCase = true)) return

        val sourceExif = source.openStream().use(::ExifInterface)
        val destinationExif = ExifInterface(destination)
        copyStandardExif(sourceExif, destinationExif)
        if (locationPolicy == LocationMetadataPolicy.PRESERVE) {
            copyLocationExif(sourceExif, destinationExif)
        }
        destinationExif.saveAttributes()
    }

    private fun copyStandardExif(source: ExifInterface, destination: ExifInterface) {
        STANDARD_EXIF_TAGS.forEach { tag ->
            source.getAttribute(tag)?.let { value -> destination.setAttribute(tag, value) }
        }
    }

    private fun copyLocationExif(source: ExifInterface, destination: ExifInterface) {
        LOCATION_EXIF_TAGS.forEach { tag ->
            source.getAttribute(tag)?.let { value -> destination.setAttribute(tag, value) }
        }
    }

    private companion object {
        val STANDARD_EXIF_TAGS = listOf(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_DATETIME_ORIGINAL,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_F_NUMBER,
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
            ExifInterface.TAG_FOCAL_LENGTH
        )
        val LOCATION_EXIF_TAGS = listOf(
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_GPS_ALTITUDE_REF,
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_GPS_TIMESTAMP
        )
    }
}
