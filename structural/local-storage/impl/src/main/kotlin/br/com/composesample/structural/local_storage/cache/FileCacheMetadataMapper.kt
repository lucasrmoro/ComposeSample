package br.com.composesample.structural.local_storage.cache

import br.com.composesample.structural.local_storage.cache.entity.CacheNamespaceEntity
import br.com.composesample.structural.local_storage.cache.entity.CachedFileMetadataEntity

internal interface FileCacheMetadataMapper {

    fun toNamespaceEntity(namespace: String): CacheNamespaceEntity
    fun toMetadata(entity: CachedFileMetadataEntity): CachedFileMetadata
    fun toEntity(metadata: CachedFileMetadata): CachedFileMetadataEntity

}
