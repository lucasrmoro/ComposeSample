package br.com.composesample.structural.local_storage.cache

import br.com.composesample.structural.local_storage.cache.entity.CachedFileMetadataEntity

internal class FileCacheMetadataMapper {

    fun toMetadata(entity: CachedFileMetadataEntity) =
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

    fun toEntity(metadata: CachedFileMetadata) =
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
