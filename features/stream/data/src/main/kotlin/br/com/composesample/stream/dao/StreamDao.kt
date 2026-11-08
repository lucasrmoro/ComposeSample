package br.com.composesample.stream.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.composesample.stream.entity.StreamCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface StreamDao {

    @Query("SELECT * FROM stream_cache WHERE `key` = :key")
    fun observe(key: String): Flow<StreamCacheEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun save(entity: StreamCacheEntity)

}
