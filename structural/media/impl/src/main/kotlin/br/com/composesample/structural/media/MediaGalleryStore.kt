package br.com.composesample.structural.media

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import java.io.IOException

internal class MediaGalleryStore(
    private val context: Context,
    private val paths: MediaStoragePaths
) {

    fun create(
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
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${paths.requireGalleryPath(location.directory)}/")
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

    fun replace(
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

    private companion object {
        const val IMAGE_MIME_TYPE_PREFIX = "image/"
        const val VIDEO_MIME_TYPE_PREFIX = "video/"
        const val MEDIA_STORE_PENDING = 1
    }

}
