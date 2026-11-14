package br.com.composesample.structural.assets_cache

import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDataSource
import br.com.composesample.structural.media.MediaLocation
import br.com.composesample.structural.media.MediaStorageProvider
import br.com.composesample.structural.network.http.BinaryDownloadProvider
import br.com.composesample.structural.security.hashing.HashingProvider
import java.time.Clock
import kotlinx.coroutines.CoroutineDispatcher

internal fun createAssetsCacheProvider(
    mediaStorageProvider: MediaStorageProvider,
    binaryDownloadProvider: BinaryDownloadProvider,
    coroutineDispatcher: CoroutineDispatcher,
    assetsCacheConfiguration: AssetsCacheConfiguration,
    hashingProvider: HashingProvider,
    fileCacheMetadataDataSource: FileCacheMetadataDataSource,
    clock: Clock = Clock.systemUTC()
): AssetsCacheProvider {
    val mediaLocation = MediaLocation.appCache(assetsCacheConfiguration.directory)
    val assetsCacheStore =
        AssetsCacheStore(
            mediaStorageProvider = mediaStorageProvider,
            fileCacheMetadataDataSource = fileCacheMetadataDataSource,
            assetsCacheMetadataMapper = AssetsCacheMetadataMapper(),
            hashingProvider = hashingProvider,
            mediaLocation = mediaLocation,
            maximumBytes = assetsCacheConfiguration.maximumBytes
        )
    val assetCacheWriter =
        AssetCacheWriter(
            mediaStorageProvider = mediaStorageProvider,
            binaryDownloadProvider = binaryDownloadProvider,
            mediaLocation = mediaLocation,
            assetsCacheConfiguration = assetsCacheConfiguration
        )
    return AssetsCacheProviderImpl(
        assetsCacheStore = assetsCacheStore,
        assetCacheWriter = assetCacheWriter,
        assetsCacheConfiguration = assetsCacheConfiguration,
        clock = clock,
        coroutineDispatcher = coroutineDispatcher
    )
}
