package br.com.composesample.stream.dataSource

import br.com.composesample.stream.dao.StreamDao
import br.com.composesample.stream.entity.StreamCacheEntity
import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

internal class StreamLocalDataSourceImpl(
    private val streamDao: StreamDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : BaseDataSource(), StreamLocalDataSource {

    override fun observe(key: String) = streamDao.observe(key).flowOn(coroutineDispatcher)

    override fun save(entity: StreamCacheEntity) = call {
        streamDao.save(entity)
    }.flowOn(coroutineDispatcher)

}
