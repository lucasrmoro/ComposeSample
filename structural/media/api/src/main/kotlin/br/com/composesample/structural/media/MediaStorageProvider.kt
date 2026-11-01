package br.com.composesample.structural.media

import java.io.IOException

/** Creates and replaces media in cache or gallery storage. */
interface MediaStorageProvider {

    /**
     * Creates a writable media item at [location]. The extension must not include a leading dot.
     *
     * @throws MediaStoragePermissionRequiredException If legacy gallery storage permission is missing.
     * @throws IOException If a storage destination cannot be created.
     */
    fun createOutput(
        location: MediaLocation,
        nameWithoutExtension: String,
        extension: String,
        mimeType: String
    ): WritableMediaFile

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
