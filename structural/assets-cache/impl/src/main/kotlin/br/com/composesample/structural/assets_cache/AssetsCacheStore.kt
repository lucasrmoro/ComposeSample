package br.com.composesample.structural.assets_cache

import br.com.composesample.structural.local_storage.cache.CachedFileMetadata
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDataSource
import br.com.composesample.structural.media.MediaFile
import br.com.composesample.structural.media.MediaLocation
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.security.hashing.HashingProvider
import java.io.IOException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

internal class AssetsCacheStore(
    private val mediaStorageProvider: MediaStorageProvider,
    private val fileCacheMetadataDataSource: FileCacheMetadataDataSource,
    private val assetsCacheMetadataMapper: AssetsCacheMetadataMapper,
    private val hashingProvider: HashingProvider,
    private val mediaLocation: MediaLocation,
    private val maximumBytes: Long
) {

    private val namespace = mediaLocation.directory

    suspend fun find(url: String): CachedAsset? {
        prepareNamespace()
        val metadata =
            fileCacheMetadataDataSource.find(namespace, hashingProvider.sha256(url)).single()
                ?: return null
        val file =
            resolve(metadata)
                ?: run {
                    fileCacheMetadataDataSource.remove(namespace, metadata.key).single()
                    return null
                }
        return CachedAsset(file = file, downloadedAt = metadata.downloadedAt)
    }

    suspend fun touch(url: String, now: Long): Result<Unit> =
        fileCacheMetadataDataSource.touch(namespace, hashingProvider.sha256(url), now)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
            .single()

    suspend fun save(url: String, file: MediaFile, now: Long): MediaFile {
        val key = hashingProvider.sha256(url)
        val previous = fileCacheMetadataDataSource.find(namespace, key).single()
        runCatching {
            trimFor(file.sizeBytes - (previous?.sizeBytes ?: 0L), key)
            fileCacheMetadataDataSource
                .save(
                    assetsCacheMetadataMapper.toMetadata(
                        namespace = namespace,
                        key = key,
                        file = file,
                        now = now
                    )
                )
                .single()
        }.onFailure {
            withContext(NonCancellable) {
                previous?.let { fileCacheMetadataDataSource.save(it).single() }
                    ?: fileCacheMetadataDataSource.remove(namespace, key).single()
                file.delete()
            }
        }.getOrThrow()
        previous?.let(::resolve)?.delete()
        return file
    }

    private suspend fun trimFor(incomingBytes: Long, protectedKey: String) {
        while (
            fileCacheMetadataDataSource.totalBytes(namespace).single() + incomingBytes >
                maximumBytes
        ) {
            val oldest =
                fileCacheMetadataDataSource.oldest(namespace, protectedKey).single()
                    ?: throw IOException(CAPACITY_MESSAGE)
            resolve(oldest)?.let { if (!it.delete()) throw IOException(DELETE_MESSAGE) }
            fileCacheMetadataDataSource.remove(namespace, oldest.key).single()
        }
    }

    private suspend fun prepareNamespace() {
        fileCacheMetadataDataSource.namespace(namespace).single()?.let {
            return
        }
        mediaStorageProvider.scanCache(mediaLocation).collect { file ->
            if (!file.delete() && file.sizeBytes > 0) throw IOException(DELETE_MESSAGE)
        }
        fileCacheMetadataDataSource.registerNamespace(namespace).single()
    }

    private fun resolve(metadata: CachedFileMetadata): MediaFile? =
        mediaStorageProvider.findCache(
            location = mediaLocation.copy(directory = metadata.directory),
            nameWithoutExtension = metadata.fileName,
            extension = metadata.extension,
            mimeType = metadata.mimeType
        )

    private companion object {
        const val CAPACITY_MESSAGE = "Asset does not fit in the cache"
        const val DELETE_MESSAGE = "Could not remove cached asset"
    }
}
