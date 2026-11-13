package br.com.composesample.stream.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.composesample.stream.dao.StreamDao
import br.com.composesample.stream.entity.StreamCacheEntity

private const val STREAM_DATABASE_NAME = "sample_data_base"

@Database(entities = [StreamCacheEntity::class], version = 3, exportSchema = false)
abstract class StreamDatabase : RoomDatabase() {

    abstract fun streamDao(): StreamDao

}

fun createStreamDao(context: Context): StreamDao =
    Room.databaseBuilder(
        context.applicationContext,
        StreamDatabase::class.java,
        STREAM_DATABASE_NAME
    ).fallbackToDestructiveMigration().build().streamDao()
