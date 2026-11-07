package br.com.composesample.structural.media

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

internal class StoredMediaFile(
    private val context: Context,
    override val location: MediaLocation,
    override var nameWithoutExtension: String,
    override val extension: String,
    override val mimeType: String,
    var file: File? = null,
    private val contentUri: Uri? = null,
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

    fun updateFile(file: File, name: String) {
        this.file = file
        updateName(name)
    }

    private fun updateName(name: String) {
        nameWithoutExtension = name
    }

    fun rename(name: String) {
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

    fun publishToMediaScanner() {
        val currentFile = file ?: return
        MediaScannerConnection.scanFile(context, arrayOf(currentFile.absolutePath), arrayOf(mimeType), null)
    }

    private companion object {
        const val FILE_PROVIDER_AUTHORITY_SUFFIX = ".structural.media.fileprovider"
        const val FILE_READ_MODE = "r"
        const val FILE_WRITE_MODE = "w"
        const val MEDIA_STORE_PUBLISHED = 0
        const val NO_ROWS_UPDATED = 0
        const val EMPTY_FILE_SIZE_BYTES = 0L
    }

}
