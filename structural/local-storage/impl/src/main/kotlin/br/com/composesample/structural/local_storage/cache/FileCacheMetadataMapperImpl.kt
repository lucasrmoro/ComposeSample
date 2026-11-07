package br.com.composesample.structural.local_storage.cache

import br.com.composesample.structural.local_storage.cache.entity.CacheNamespaceEntity
import br.com.composesample.structural.local_storage.cache.entity.CachedFileMetadataEntity

internal class FileCacheMetadataMapperImpl : FileCacheMetadataMapper {

    override fun toNamespaceEntity(namespace: String): CacheNamespaceEntity =
        CacheNamespaceEntity(namespace = namespace)

    override fun toMetadata(entity: CachedFileMetadataEntity) =
        CachedFileMetadata(
            namespace = entity.namespace,
            key = entity.key,
            directory = entity.directory,
            fileName = entity.fileName,
            extension = entity.extension,
            mimeType = entity.mimeType,
            sizeBytes = entity.sizeBytes,
            downloadedAt = entity.downloadedAt,
            lastUsedAt = entity.lastUsedAt
        )

    override fun toEntity(metadata: CachedFileMetadata) =
        CachedFileMetadataEntity(
            namespace = metadata.namespace,
            key = metadata.key,
            directory = metadata.directory,
            fileName = metadata.fileName,
            extension = metadata.extension,
            mimeType = metadata.mimeType,
            sizeBytes = metadata.sizeBytes,
            downloadedAt = metadata.downloadedAt,
            lastUsedAt = metadata.lastUsedAt
        )

}
