package br.com.composesample.structural.assets_cache

import br.com.composesample.structural.local_storage.cache.CachedFileMetadata
import br.com.composesample.structural.media.MediaFile

internal class AssetsCacheMetadataMapper {

    fun toMetadata(namespace: String, key: String, file: MediaFile, now: Long) =
        CachedFileMetadata(
            namespace = namespace,
            key = key,
            directory = file.location.directory,
            fileName = file.nameWithoutExtension,
            extension = file.extension,
            mimeType = file.mimeType,
            sizeBytes = file.sizeBytes,
            downloadedAt = now,
            lastUsedAt = now
        )

}
