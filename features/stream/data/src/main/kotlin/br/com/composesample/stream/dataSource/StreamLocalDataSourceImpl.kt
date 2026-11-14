package br.com.composesample.stream.dataSource

import android.content.Context
import br.com.composesample.stream.dao.StreamDao
import br.com.composesample.stream.database.createStreamDao
import br.com.composesample.stream.entity.StreamCacheEntity
import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class StreamLocalDataSourceImpl(
    private val streamDao: StreamDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : BaseDataSource(), StreamLocalDataSource {

    override fun observe(key: String) =
        streamDao.observe(key).map { entity -> entity?.payload }.flowOn(coroutineDispatcher)

    override fun save(key: String, payload: String) =
        call { streamDao.save(StreamCacheEntity(key = key, payload = payload)) }
            .flowOn(coroutineDispatcher)

}

fun createStreamLocalDataSource(
    context: Context,
    coroutineDispatcher: CoroutineDispatcher
): StreamLocalDataSource =
    StreamLocalDataSourceImpl(
        streamDao = createStreamDao(context = context),
        coroutineDispatcher = coroutineDispatcher
    )
