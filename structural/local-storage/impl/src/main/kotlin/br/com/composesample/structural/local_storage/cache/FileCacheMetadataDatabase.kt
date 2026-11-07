package br.com.composesample.structural.local_storage.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.composesample.structural.local_storage.cache.dao.FileCacheMetadataDao
import br.com.composesample.structural.local_storage.cache.entity.CacheNamespaceEntity
import br.com.composesample.structural.local_storage.cache.entity.CachedFileMetadataEntity

@Database(
    entities = [CachedFileMetadataEntity::class, CacheNamespaceEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class FileCacheMetadataDatabase : RoomDatabase() {

    abstract fun fileCacheMetadataDao(): FileCacheMetadataDao

}
