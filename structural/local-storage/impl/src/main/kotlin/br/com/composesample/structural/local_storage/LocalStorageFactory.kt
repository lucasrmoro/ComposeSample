package br.com.composesample.structural.local_storage

import android.content.Context
import androidx.room.Room
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDatabase
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDataSource
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDataSourceImpl
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataMapper
import br.com.composesample.featureA.dao.SomeDao
import br.com.composesample.structural.local_storage.db.LocalDatabase
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSourceImpl
import br.com.composesample.structural.local_storage.prefs.PreferencesDataStoreFactory
import kotlinx.coroutines.CoroutineDispatcher

class LocalStorageFactory(context: Context, private val ioDispatcher: CoroutineDispatcher) {

    private val applicationContext = context.applicationContext

    private val dataStoreFactory = PreferencesDataStoreFactory(applicationContext, ioDispatcher)

    private val database by lazy {
        Room.databaseBuilder(applicationContext, LocalDatabase::class.java, ROOM_DATABASE_NAME).build()
    }

    private val fileCacheMetadataDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            FileCacheMetadataDatabase::class.java,
            FILE_CACHE_DATABASE_NAME
        ).build()
    }

    fun createFileCacheMetadataDataSource(): FileCacheMetadataDataSource =
        FileCacheMetadataDataSourceImpl(
            fileCacheMetadataDao = fileCacheMetadataDatabase.fileCacheMetadataDao(),
            fileCacheMetadataMapper = FileCacheMetadataMapper(),
            coroutineDispatcher = ioDispatcher
        )

    fun createPreferencesDataSource(): PreferencesDataSource =
        PreferencesDataSourceImpl(dataStoreFactory, ioDispatcher)

    fun someDao(): SomeDao = database.fooDao()

    private companion object {
        const val FILE_CACHE_DATABASE_NAME = "file_cache_metadata"

        const val ROOM_DATABASE_NAME = "sample_data_base"
    }

}
