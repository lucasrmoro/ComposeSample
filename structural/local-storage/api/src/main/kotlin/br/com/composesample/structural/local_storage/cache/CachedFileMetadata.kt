package br.com.composesample.structural.local_storage.cache

/** File metadata only; asset contents remain in private app cache files. */
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
