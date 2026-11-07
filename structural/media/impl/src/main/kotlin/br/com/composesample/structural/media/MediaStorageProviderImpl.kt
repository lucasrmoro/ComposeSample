package br.com.composesample.structural.media

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import br.com.composesample.structural.permissions.Permission
import br.com.composesample.structural.permissions.PermissionsProvider
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class MediaStorageProviderImpl(
    context: Context,
    private val permissionsProvider: PermissionsProvider,
    private val coroutineDispatcher: CoroutineDispatcher
) : MediaStorageProvider {

    private val context = context.applicationContext

    private companion object {
        const val FILE_PROVIDER_AUTHORITY_SUFFIX = ".structural.media.fileprovider"
        const val BACKUP_FILE_SUFFIX = ".backup"
        const val FILE_READ_MODE = "r"
        const val FILE_WRITE_MODE = "w"
        const val IMAGE_MIME_TYPE_PREFIX = "image/"
        const val VIDEO_MIME_TYPE_PREFIX = "video/"
        const val MEDIA_STORE_PENDING = 1
        const val MEDIA_STORE_PUBLISHED = 0
        const val NO_ROWS_UPDATED = 0
        const val EMPTY_FILE_SIZE_BYTES = 0L
        const val GALLERY_ROOT_DIRECTORY_SEGMENT_COUNT = 1
        const val BINARY_MIME_TYPE = "application/octet-stream"
        const val CACHE_PUBLISH_FAILURE = "Could not publish cached file"
    }

    override fun createOutput(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): WritableMediaFile {
        val safeName = requireName(nameWithoutExtension)
        val safeExtension = requireExtension(extension)
        if (location.area == MediaStorageArea.GALLERY && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            !permissionsProvider.isGranted(Permission.WRITE_EXTERNAL_STORAGE)
        ) {
            throw MediaStoragePermissionRequiredException()
        }

        return if (location.area == MediaStorageArea.GALLERY && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createMediaStoreFile(location, safeName, safeExtension, mimeType)
        } else {
            createFile(location, safeName, safeExtension, mimeType)
        }
    }

    override fun scanCache(location: MediaLocation): Flow<MediaFile> = flow {
        require(location.area == MediaStorageArea.APP_CACHE)
        val directory = getDirectory(location)
        directory.walkTopDown().filter(File::isFile).forEach { file ->
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
        .flowOn(coroutineDispatcher)

    override fun findCache(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): MediaFile? {
        require(location.area == MediaStorageArea.APP_CACHE)
        val name = requireName(nameWithoutExtension)
        val safeExtension = requireExtension(extension)
        val file = File(getDirectory(location), "$name.$safeExtension")
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

    override fun publishCache(source: WritableMediaFile, extension: String, mimeType: String): MediaFile {
        require(source.location.area == MediaStorageArea.APP_CACHE)
        val current = requireNotNull(source.requireStoredFile().file)
        val safeExtension = requireExtension(extension)
        val destination = File(current.parentFile, "${source.nameWithoutExtension}.$safeExtension")
        if (current != destination && (destination.exists() || !current.renameTo(destination))) throw IOException(CACHE_PUBLISH_FAILURE)
        return StoredMediaFile(
            context = context, location = source.location, nameWithoutExtension = source.nameWithoutExtension,
            extension = safeExtension, mimeType = mimeType, file = destination, pendingMediaStoreItem = false
        )
    }

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

        val safeName = requireName(nameWithoutExtension)
        return if (sourceFile.file != null && replacementFile.file != null) {
            replaceFile(sourceFile, replacementFile, safeName)
        } else {
            replaceMediaStoreFile(sourceFile, replacementFile, safeName)
        }
    }

    private fun createFile(
        location: MediaLocation,
        name: String,
        extension: String,
        mimeType: String
    ): StoredMediaFile {
        val directory = getDirectory(location)
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

    private fun createMediaStoreFile(
        location: MediaLocation,
        name: String,
        extension: String,
        mimeType: String
    ): StoredMediaFile {
        val collection = when {
            mimeType.startsWith(IMAGE_MIME_TYPE_PREFIX) -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mimeType.startsWith(VIDEO_MIME_TYPE_PREFIX) -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> throw IOException("Gallery storage does not support $mimeType")
        }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.$extension")
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${requireGalleryPath(location.directory)}/")
            put(MediaStore.MediaColumns.IS_PENDING, MEDIA_STORE_PENDING)
        }
        val uri = context.contentResolver.insert(collection, values)
            ?: throw IOException("Could not create gallery media entry")
        return StoredMediaFile(
            context = context,
            location = location,
            nameWithoutExtension = name,
            extension = extension,
            mimeType = mimeType,
            contentUri = uri,
            pendingMediaStoreItem = true
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

    private fun getDirectory(location: MediaLocation): File {
        val parts = requireRelativePath(location.directory)
        return when (location.area) {
            MediaStorageArea.APP_CACHE -> File(
                File(context.cacheDir, MediaLocation.DEFAULT_CACHE_DIRECTORY),
                parts.joinToString("/")
            )
            MediaStorageArea.GALLERY -> {
                val rootName = parts.firstOrNull()
                if (rootName != Environment.DIRECTORY_PICTURES && rootName != Environment.DIRECTORY_MOVIES) {
                    throw IOException("Gallery media must be stored under Pictures or Movies")
                }
                val root = Environment.getExternalStoragePublicDirectory(rootName)
                File(
                    root,
                    parts.drop(GALLERY_ROOT_DIRECTORY_SEGMENT_COUNT).joinToString("/")
                )
            }
        }
    }

    private fun requireName(value: String): String {
        val name = value.trim()
        require(name.isNotEmpty() && name != "." && name != ".." &&
            '/' !in name && '\\' !in name && '.' !in name
        ) { "Media names must be a single base name without an extension" }
        return name
    }

    private fun requireExtension(value: String): String {
        val extension = value.removePrefix(".")
        require(extension.isNotBlank() && extension.all { it.isLetterOrDigit() }) {
            "Media extensions may contain only letters and digits"
        }
        return extension
    }

    private fun requireRelativePath(value: String): List<String> {
        val parts = value.split('/').filter(String::isNotBlank)
        require(parts.isNotEmpty() && parts.none { it == "." || it == ".." }) {
            "Media locations must use a non-empty relative directory"
        }
        return parts
    }

    private fun requireGalleryPath(value: String): String {
        val parts = requireRelativePath(value)
        require(parts.first() == Environment.DIRECTORY_PICTURES ||
            parts.first() == Environment.DIRECTORY_MOVIES
        ) { "Gallery media must be stored under Pictures or Movies" }
        return parts.joinToString("/")
    }

    private fun MediaFile.requireStoredFile(): StoredMediaFile = this as? StoredMediaFile
        ?: throw IllegalArgumentException("Media was not created by this storage provider")

    private fun WritableMediaFile.requireStoredFile(): StoredMediaFile = this as? StoredMediaFile
        ?: throw IllegalArgumentException("Media was not created by this storage provider")

    private fun StoredMediaFile.updateFile(file: File, name: String) {
        this.file = file
        updateName(name)
    }

    private fun StoredMediaFile.updateName(name: String) {
        nameWithoutExtension = name
    }

    private fun StoredMediaFile.rename(name: String) {
        val uri = contentUri ?: return
        val renamed = "$name.$extension"
        if (context.contentResolver.update(
                uri,
                ContentValues().apply { put(MediaStore.MediaColumns.DISPLAY_NAME, renamed) },
                null,
                null
            ) <= NO_ROWS_UPDATED
        ) throw IOException("Could not rename replacement media")
        updateName(name)
    }

    private fun StoredMediaFile.publishToMediaScanner() {
        val currentFile = file ?: return
        MediaScannerConnection.scanFile(context, arrayOf(currentFile.absolutePath), arrayOf(mimeType), null)
    }

    private fun replaceMediaStoreFile(
        source: StoredMediaFile,
        replacement: StoredMediaFile,
        name: String
    ): MediaFile {
        replacement.rename(name)
        if (!replacement.publish()) throw IOException("Could not publish compressed gallery media")
        if (!source.delete()) {
            replacement.delete()
            throw IOException("Could not remove original gallery media")
        }
        return replacement
    }

    private inner class StoredMediaFile(
        val context: Context,
        override val location: MediaLocation,
        override var nameWithoutExtension: String,
        override val extension: String,
        override val mimeType: String,
        var file: File? = null,
        val contentUri: Uri? = null,
        private val pendingMediaStoreItem: Boolean
    ) : WritableMediaFile {

        private val resolver: ContentResolver = context.contentResolver

        override val uri: String
            get() = file?.let {
                FileProvider.getUriForFile(
                    context,
                    context.packageName + FILE_PROVIDER_AUTHORITY_SUFFIX,
                    it
                ).toString()
            } ?: contentUri.toString()

        override val sizeBytes: Long
            get() = file?.length() ?: runCatching {
                resolver.openFileDescriptor(contentUri!!, FILE_READ_MODE)?.use { it.statSize } ?: EMPTY_FILE_SIZE_BYTES
            }.getOrDefault(0L)

        override fun openStream(): InputStream = file?.inputStream()
            ?: resolver.openInputStream(contentUri!!)
            ?: throw IOException("Media file is not readable")

        override fun openOutputStream(): OutputStream = file?.outputStream()
            ?: resolver.openOutputStream(contentUri!!, FILE_WRITE_MODE)
            ?: throw IOException("Media file is not writable")

        override fun publish(): Boolean = if (!pendingMediaStoreItem) {
            if (location.area == MediaStorageArea.GALLERY && file != null) {
                publishToMediaScanner()
            }
            true
        } else {
            resolver.update(
                contentUri!!,
                ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, MEDIA_STORE_PUBLISHED) },
                null,
                null
            ) > NO_ROWS_UPDATED
        }

        override fun discard(): Boolean = delete()

        override fun delete(): Boolean = file?.delete()
            ?: (resolver.delete(contentUri!!, null, null) > NO_ROWS_UPDATED)

    }

}
