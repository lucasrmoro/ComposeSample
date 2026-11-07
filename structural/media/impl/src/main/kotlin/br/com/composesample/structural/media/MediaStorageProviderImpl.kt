package br.com.composesample.structural.media

import android.content.Context
import android.os.Build
import br.com.composesample.structural.permissions.Permission
import br.com.composesample.structural.permissions.PermissionsProvider
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.flow.Flow

internal class MediaStorageProviderImpl(
    private val context: Context,
    private val permissionsProvider: PermissionsProvider,
    private val paths: MediaStoragePaths,
    private val cacheStore: MediaCacheStore,
    private val galleryStore: MediaGalleryStore
) : MediaStorageProvider {

    override fun createOutput(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): WritableMediaFile {
        val safeName = paths.requireName(nameWithoutExtension)
        val safeExtension = paths.requireExtension(extension)
        if (location.area == MediaStorageArea.GALLERY && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            !permissionsProvider.isGranted(Permission.WRITE_EXTERNAL_STORAGE)
        ) {
            throw MediaStoragePermissionRequiredException()
        }

        return if (location.area == MediaStorageArea.GALLERY && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            galleryStore.create(location, safeName, safeExtension, mimeType)
        } else {
            createFile(location, safeName, safeExtension, mimeType)
        }
    }

    override fun scanCache(location: MediaLocation): Flow<MediaFile> = cacheStore.scanCache(location)

    override fun findCache(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): MediaFile? = cacheStore.findCache(location, nameWithoutExtension, extension, mimeType)

    override fun createOutput(
        location: MediaLocation,
        nameWithoutExtension: String,
        writer: MediaContentWriter
    ): Flow<MediaFile> = cacheStore.createOutput(location, nameWithoutExtension, writer)

    override fun replace(
        source: MediaFile,
        replacement: WritableMediaFile,
        nameWithoutExtension: String
    ): MediaFile {
        val sourceFile = source.requireStoredFile()
        val replacementFile = replacement.requireStoredFile()
        require(source.location == replacement.location) {
            "Replacement media must use the source location"
        }
        require(source.uri != replacement.uri) { "Source and replacement must be different media files" }

        val safeName = paths.requireName(nameWithoutExtension)
        return if (sourceFile.file != null && replacementFile.file != null) {
            replaceFile(sourceFile, replacementFile, safeName)
        } else {
            galleryStore.replace(sourceFile, replacementFile, safeName)
        }
    }

    private fun createFile(
        location: MediaLocation,
        name: String,
        extension: String,
        mimeType: String
    ): StoredMediaFile {
        val directory = paths.getDirectory(location)
        if (!directory.exists() && !directory.mkdirs()) {
            throw IOException("Could not create media directory")
        }

        val file = File(directory, "$name.$extension")
        if (!file.createNewFile()) throw IOException("Media file already exists")
        return StoredMediaFile(
            context = context,
            location = location,
            nameWithoutExtension = name,
            extension = extension,
            mimeType = mimeType,
            file = file,
            pendingMediaStoreItem = false
        )
    }

    private fun replaceFile(
        source: StoredMediaFile,
        replacement: StoredMediaFile,
        name: String
    ): MediaFile {
        val sourcePath = requireNotNull(source.file)
        val outputPath = requireNotNull(replacement.file)
        val finalPath = File(sourcePath.parentFile, "$name.${replacement.extension}")
        val backup = File(sourcePath.parentFile, ".${UUID.randomUUID()}$BACKUP_FILE_SUFFIX")

        if (!sourcePath.renameTo(backup)) throw IOException("Could not stage original media for replacement")
        if (finalPath.exists() || !outputPath.renameTo(finalPath)) {
            backup.renameTo(sourcePath)
            throw IOException("Could not move compressed media into place")
        }
        if (!backup.delete()) {
            finalPath.renameTo(outputPath)
            backup.renameTo(sourcePath)
            throw IOException("Could not remove original media after replacement")
        }

        replacement.updateFile(finalPath, name)
        if (replacement.location.area == MediaStorageArea.GALLERY) {
            replacement.publishToMediaScanner()
        }
        return replacement
    }

    private fun MediaFile.requireStoredFile(): StoredMediaFile = this as? StoredMediaFile
        ?: throw IllegalArgumentException("Media was not created by this storage provider")

    private companion object {
        const val BACKUP_FILE_SUFFIX = ".backup"
    }

}
