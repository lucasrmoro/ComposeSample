package br.com.composesample.structural.local_storage.cache

/**
 * File metadata only; asset contents remain in private app cache files.
 *
 * [namespace] and [key] together identify an entry. [directory] is relative to the app's media
 * cache; [fileName] excludes the extension and [extension] excludes the leading dot. [sizeBytes]
 * is the recorded size, not a live filesystem measurement. [downloadedAt] and [lastUsedAt] are
 * caller-supplied epoch timestamps in milliseconds, stored without recalculation.
 */
data class CachedFileMetadata(
    val namespace: String,
    val key: String,
    val directory: String,
    val fileName: String,
    val extension: String,
    val mimeType: String,
    val sizeBytes: Long,
    val downloadedAt: Long,
    val lastUsedAt: Long
)
