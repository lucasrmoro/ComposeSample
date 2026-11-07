package br.com.composesample.structural.local_storage.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.composesample.structural.local_storage.cache.entity.CacheNamespaceEntity
import br.com.composesample.structural.local_storage.cache.entity.CachedFileMetadataEntity

@Dao
internal interface FileCacheMetadataDao {

    @Query("SELECT namespace FROM cache_namespaces WHERE namespace = :namespace LIMIT 1")
    suspend fun namespace(namespace: String): String?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registerNamespace(entity: CacheNamespaceEntity)
    @Query(
        "SELECT * FROM cached_file_metadata WHERE namespace = :namespace AND `key` = :key LIMIT 1"
    )
    suspend fun find(namespace: String, key: String): CachedFileMetadataEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: CachedFileMetadataEntity)
    @Query(
        "UPDATE cached_file_metadata SET lastUsedAt = :lastUsedAt WHERE namespace = :namespace AND `key` = :key"
    )
    suspend fun touch(namespace: String, key: String, lastUsedAt: Long)
    @Query("DELETE FROM cached_file_metadata WHERE namespace = :namespace AND `key` = :key")
    suspend fun remove(namespace: String, key: String)
    @Query(
        "SELECT COALESCE(SUM(sizeBytes), 0) FROM cached_file_metadata WHERE namespace = :namespace"
    )
    suspend fun totalBytes(namespace: String): Long
    @Query(
        "SELECT * FROM cached_file_metadata WHERE namespace = :namespace AND `key` != :excludedKey ORDER BY lastUsedAt ASC, `key` ASC LIMIT 1"
    )
    suspend fun oldest(namespace: String, excludedKey: String): CachedFileMetadataEntity?

}
