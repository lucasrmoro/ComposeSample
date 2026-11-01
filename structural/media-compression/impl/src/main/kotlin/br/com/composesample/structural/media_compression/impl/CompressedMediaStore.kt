package br.com.composesample.structural.media_compression.impl

import br.com.composesample.structural.media.MediaFile
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.media.WritableMediaFile
import br.com.composesample.structural.media_compression.api.CompressionOutputMode
import java.io.File
import java.io.IOException

internal class CompressedMediaStore(
    private val storageProvider: MediaStorageProvider
) {

    fun save(
        source: MediaFile,
        compressedFile: File,
        outputMode: CompressionOutputMode,
        outputNameWithoutExtension: String?,
        extension: String,
        mimeType: String
    ): MediaFile {
        val temporaryName = when (outputMode) {
            CompressionOutputMode.REPLACE ->
                "${source.nameWithoutExtension}${MediaCompressionConstants.REPLACEMENT_NAME_INFIX}${System.nanoTime()}"
            CompressionOutputMode.COPY -> outputNameWithoutExtension
                ?: "${source.nameWithoutExtension}${MediaCompressionConstants.COMPRESSED_COPY_NAME_SUFFIX}"
        }
        val output = storageProvider.createOutput(
            location = source.location,
            nameWithoutExtension = temporaryName,
            extension = extension,
            mimeType = mimeType
        )
        return try {
            copyToOutput(compressedFile, output)
            if (!output.publish()) throw IOException("Could not publish compressed media")
            when (outputMode) {
                CompressionOutputMode.REPLACE -> storageProvider.replace(
                    source = source,
                    replacement = output,
                    nameWithoutExtension = outputNameWithoutExtension ?: source.nameWithoutExtension
                )
                CompressionOutputMode.COPY -> output
            }
        } catch (exception: Exception) {
            output.discard()
            throw exception
        }
    }

    private fun copyToOutput(sourceFile: File, output: WritableMediaFile) {
        sourceFile.inputStream().use { source ->
            output.openOutputStream().use { target -> source.copyTo(target) }
        }
    }

}
