package br.com.composesample.structural.media

import android.content.Context
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

internal class MediaCacheStore(
    private val context: Context,
    private val paths: MediaStoragePaths,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    fun scanCache(location: MediaLocation): Flow<MediaFile> = flow {
        require(location.area == MediaStorageArea.APP_CACHE)
        coroutineScope {
            launch(coroutineDispatcher) {
                removeAbandonedTemporaryFiles()
            }
            val directory = paths.getDirectory(location)
            directory.walkTopDown().filter(File::isFile).forEach { file ->
                currentCoroutineContext().ensureActive()
                val fileLocation =
                    file.relativeTo(directory).parent?.let {
                        location.copy(directory = "${location.directory}/$it")
                    } ?: location
                emit(
                    StoredMediaFile(
                        context,
                        fileLocation,
                        file.nameWithoutExtension,
                        file.extension,
                        BINARY_MIME_TYPE,
                        file = file,
                        pendingMediaStoreItem = false
                    )
                )
            }
        }
    }.flowOn(coroutineDispatcher)

    private suspend fun removeAbandonedTemporaryFiles() {
        val temporaryFiles = try {
            context.cacheDir.listFiles { file ->
                file.isFile && file.name.startsWith(TEMPORARY_FILE_PREFIX) &&
                    file.name.endsWith(TEMPORARY_FILE_SUFFIX)
            }
        } catch (_: SecurityException) {
            return
        }
        temporaryFiles?.forEach { file ->
            currentCoroutineContext().ensureActive()
            removeAbandonedTemporaryFile(file)
        }
    }

    private fun removeAbandonedTemporaryFile(file: File) {
        synchronized(activeTemporaryFiles) {
            if (file !in activeTemporaryFiles) {
                try {
                    file.delete()
                } catch (_: SecurityException) {
                    return
                }
            }
        }
    }

    fun findCache(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): MediaFile? {
        require(location.area == MediaStorageArea.APP_CACHE)
        val name = paths.requireName(nameWithoutExtension)
        val safeExtension = paths.requireExtension(extension)
        val file = File(paths.getDirectory(location), "$name.$safeExtension")
        return file.takeIf(File::isFile)?.let {
            StoredMediaFile(
                context,
                location,
                name,
                safeExtension,
                mimeType,
                file = it,
                pendingMediaStoreItem = false
            )
        }
    }

    fun createOutput(
        location: MediaLocation,
        nameWithoutExtension: String,
        writer: MediaContentWriter
    ): Flow<MediaFile> = flow {
        require(location.area == MediaStorageArea.APP_CACHE)
        val name = paths.requireName(nameWithoutExtension)
        val directory = prepareDirectory(location)
        val temporaryFile = createTemporaryFile()
        var ownedFile = temporaryFile
        try {
            val format = writeContent(temporaryFile, writer)
            currentCoroutineContext().ensureActive()
            ownedFile = finalizeCacheFile(temporaryFile, directory, name, format)
            emit(completedCacheFile(ownedFile, location, name, format))
        } catch (failure: Throwable) {
            discardFailedWrite(ownedFile, failure)
            throw failure
        } finally {
            activeTemporaryFiles.remove(temporaryFile)
        }
    }.flowOn(coroutineDispatcher)

    private fun prepareDirectory(location: MediaLocation): File {
        val directory = paths.getDirectory(location)
        if (!directory.exists() && !directory.mkdirs()) {
            throw IOException("Could not create media directory")
        }
        return directory
    }

    private fun createTemporaryFile(): File = synchronized(activeTemporaryFiles) {
        File.createTempFile(TEMPORARY_FILE_PREFIX, TEMPORARY_FILE_SUFFIX, context.cacheDir).also {
            activeTemporaryFiles.add(it)
        }
    }

    private suspend fun writeContent(file: File, writer: MediaContentWriter): MediaFormat =
        file.outputStream().use { writer.write(it).single() }

    private fun finalizeCacheFile(
        temporaryFile: File,
        directory: File,
        name: String,
        format: MediaFormat
    ): File {
        val extension = paths.requireExtension(format.extension)
        val destination = File(directory, "$name.$extension")
        Files.move(temporaryFile.toPath(), destination.toPath())
        return destination
    }

    private fun completedCacheFile(
        file: File,
        location: MediaLocation,
        name: String,
        format: MediaFormat
    ): MediaFile = StoredMediaFile(
        context = context,
        location = location,
        nameWithoutExtension = name,
        extension = file.extension,
        mimeType = format.mimeType,
        file = file,
        pendingMediaStoreItem = false
    )

    private fun discardFailedWrite(file: File, failure: Throwable) {
        try {
            if (!file.delete() && file.exists()) {
                failure.addSuppressed(IOException("Could not remove incomplete media file"))
            }
        } catch (cleanupFailure: Throwable) {
            failure.addSuppressed(cleanupFailure)
        }
    }

    private companion object {
        const val BINARY_MIME_TYPE = "application/octet-stream"
        const val TEMPORARY_FILE_PREFIX = "media-write-"
        const val TEMPORARY_FILE_SUFFIX = ".part"

        val activeTemporaryFiles = ConcurrentHashMap.newKeySet<File>()
    }

}
