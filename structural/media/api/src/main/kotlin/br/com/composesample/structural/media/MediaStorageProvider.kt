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

    /**
     * Recursively emits files from the app's private cache at [location] on the injected I/O
     * dispatcher. Each collection starts a new scan; subsequent changes are not observed.
     *
     * Each scan also removes abandoned temporary files from managed writes in parallel on the I/O
     * dispatcher, excluding active writes in this process. These temporary files are never emitted.
     * Collection waits for cleanup to finish and fails with [IOException] if deletion fails.
     *
     * Only [MediaStorageArea.APP_CACHE] is supported. Gallery locations are rejected without
     * enumerating or accessing the user's gallery media.
     *
     * @throws IllegalArgumentException When collected with a location outside the app's private cache.
     */
    fun scanCache(location: MediaLocation): Flow<MediaFile>

    /** Resolves a private cache file directly, returning null when Android removed it. */
    fun findCache(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): MediaFile?

    /**
     * Writes and finalizes a private cache file on the injected I/O dispatcher when collected.
     * [writer] must emit exactly one final format and finish all writes before its flow completes.
     * The provider closes the stream before committing the file and emits one completed reference.
     * Each collection performs a new write; an existing destination is never overwritten.
     *
     * Temporary files are outside the scanned media cache. Failure or cancellation removes the
     * temporary file, or the committed file if delivery fails, and propagates the original error.
     * Cleanup failures are attached as suppressed exceptions; an undeleted temporary file remains
     * outside cache scans. The writer must cooperate with coroutine cancellation.
     *
     * @throws IllegalArgumentException If [location] is not private cache or the name/format is invalid.
     * @throws IOException If creation, writing or finalization fails.
     */
    fun createOutput(
        location: MediaLocation,
        nameWithoutExtension: String,
        writer: MediaContentWriter
    ): Flow<MediaFile>

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
