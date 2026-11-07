package br.com.composesample.structural.local_storage.cache

import kotlinx.coroutines.flow.Flow

/**
 * Performs bounded metadata queries without loading the complete cache catalog into memory.
 * Each cold Flow performs one operation and emits one result per collection.
 */
interface FileCacheMetadataDataSource {

    fun namespace(namespace: String): Flow<String?>
    fun registerNamespace(namespace: String): Flow<Unit>
    fun find(namespace: String, key: String): Flow<CachedFileMetadata?>
    fun save(metadata: CachedFileMetadata): Flow<Unit>
    fun touch(namespace: String, key: String, lastUsedAt: Long): Flow<Unit>
    fun remove(namespace: String, key: String): Flow<Unit>
    fun totalBytes(namespace: String): Flow<Long>
    fun oldest(namespace: String, excludedKey: String): Flow<CachedFileMetadata?>

}
