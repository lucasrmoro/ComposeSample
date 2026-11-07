package br.com.composesample.structural.local_storage.cache

import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import br.com.composesample.structural.local_storage.cache.dao.FileCacheMetadataDao
import br.com.composesample.structural.local_storage.cache.entity.CacheNamespaceEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

internal class FileCacheMetadataDataSourceImpl(
    private val fileCacheMetadataDao: FileCacheMetadataDao,
    private val fileCacheMetadataMapper: FileCacheMetadataMapper,
    private val coroutineDispatcher: CoroutineDispatcher
) : BaseDataSource(), FileCacheMetadataDataSource {

    override fun namespace(namespace: String) = call {
        fileCacheMetadataDao.namespace(namespace)
    }.flowOn(coroutineDispatcher)

    override fun registerNamespace(namespace: String) = call {
        fileCacheMetadataDao.registerNamespace(CacheNamespaceEntity(namespace = namespace))
    }
        .flowOn(coroutineDispatcher)

    override fun find(namespace: String, key: String) = call {
        fileCacheMetadataDao.find(namespace, key)?.let(fileCacheMetadataMapper::toMetadata)
    }
        .flowOn(coroutineDispatcher)

    override fun save(metadata: CachedFileMetadata) = call {
        fileCacheMetadataDao.save(fileCacheMetadataMapper.toEntity(metadata))
    }
        .flowOn(coroutineDispatcher)

    override fun touch(namespace: String, key: String, lastUsedAt: Long) = call {
        fileCacheMetadataDao.touch(namespace, key, lastUsedAt)
    }
        .flowOn(coroutineDispatcher)

    override fun remove(namespace: String, key: String) = call {
        fileCacheMetadataDao.remove(namespace, key)
    }.flowOn(coroutineDispatcher)

    override fun totalBytes(namespace: String) = call {
        fileCacheMetadataDao.totalBytes(namespace)
    }.flowOn(coroutineDispatcher)

    override fun oldest(namespace: String, excludedKey: String) = call {
        fileCacheMetadataDao
            .oldest(namespace, excludedKey)
            ?.let(fileCacheMetadataMapper::toMetadata)
    }
        .flowOn(coroutineDispatcher)

}
