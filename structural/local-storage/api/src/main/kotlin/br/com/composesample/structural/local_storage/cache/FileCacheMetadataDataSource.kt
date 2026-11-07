package br.com.composesample.structural.local_storage.cache

import kotlinx.coroutines.flow.Flow

/**
 * Performs bounded metadata queries without loading the complete cache catalog into memory.
 * Each cold Flow performs one operation on the injected I/O dispatcher and emits one result per
 * collection. Storage errors propagate to the collector. Operations affect metadata only, never
 * the referenced files. Namespace registration is independent of metadata storage.
 */
interface FileCacheMetadataDataSource {

    /** Returns the registered namespace, or null when it has not been registered. */
    fun namespace(namespace: String): Flow<String?>

    /** Registers a namespace; an existing registration is left unchanged. */
    fun registerNamespace(namespace: String): Flow<Unit>

    /** Returns the matching entry, or null when the namespace/key pair is absent. */
    fun find(namespace: String, key: String): Flow<CachedFileMetadata?>

    /** Inserts metadata or replaces all fields of the entry with the same namespace/key pair. */
    fun save(metadata: CachedFileMetadata): Flow<Unit>

    /** Updates the last-use epoch timestamp in milliseconds; a missing entry is left absent. */
    fun touch(namespace: String, key: String, lastUsedAt: Long): Flow<Unit>

    /** Removes the matching metadata; a missing entry is ignored and its file is never deleted. */
    fun remove(namespace: String, key: String): Flow<Unit>

    /** Sums stored sizes in the namespace, returning zero when no entries exist. */
    fun totalBytes(namespace: String): Flow<Long>

    /**
     * Returns the entry with the smallest last-use timestamp, excluding [excludedKey], or null
     * when no candidate exists. Equal timestamps are ordered by key in ascending order.
     */
    fun oldest(namespace: String, excludedKey: String): Flow<CachedFileMetadata?>

}
