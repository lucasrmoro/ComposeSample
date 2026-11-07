package br.com.composesample.structural.media

import androidx.annotation.RequiresPermission
import br.com.composesample.structural.permissions.PermissionNames
import kotlinx.coroutines.flow.Flow
import java.io.IOException

/** Creates and replaces media in cache or gallery storage. */
interface MediaStorageProvider {

    /**
     * Creates a writable media item at [location]. The extension must not include a leading dot.
     *
     * Before gallery writes on Android 9 or lower, callers must check and request
     * WRITE_EXTERNAL_STORAGE through the permissions service. Private cache writes and
     * gallery writes on Android 10+ do not require that permission.
     *
     * @throws MediaStoragePermissionRequiredException If legacy gallery storage permission is missing.
     * @throws IOException If a storage destination cannot be created.
     */
    @RequiresPermission(value = PermissionNames.WRITE_EXTERNAL_STORAGE, conditional = true)
    fun createOutput(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): WritableMediaFile

    /** Emits private cache files incrementally on the injected I/O dispatcher. */
    fun scanCache(location: MediaLocation): Flow<MediaFile>

    /** Resolves a private cache file directly, returning null when Android removed it. */
    fun findCache(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): MediaFile?

    /** Finalizes a private temporary file with the downloaded MIME type and validated extension. */
    fun publishCache(source: WritableMediaFile, extension: String, mimeType: String): MediaFile

    /**
     * Replaces [source] with [replacement] in the same location and returns its new reference.
     * The original remains intact if replacement cannot be completed.
     *
     * @throws IOException If the replacement cannot be committed.
     */
    fun replace(
        source: MediaFile,
        replacement: WritableMediaFile,
        nameWithoutExtension: String
    ): MediaFile

}
