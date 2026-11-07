package br.com.composesample.stream.dataSource

import br.com.composesample.stream.entity.StreamCacheEntity
import kotlinx.coroutines.flow.Flow

interface StreamLocalDataSource {

    fun observe(key: String): Flow<StreamCacheEntity?>

    fun save(entity: StreamCacheEntity): Flow<Unit>

}
