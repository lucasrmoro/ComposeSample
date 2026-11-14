package br.com.composesample.structural.local_storage

import android.content.Context
import androidx.room.Room
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDataSource
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDataSourceImpl
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataDatabase
import br.com.composesample.structural.local_storage.cache.FileCacheMetadataMapperImpl
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSourceImpl
import br.com.composesample.structural.local_storage.prefs.PreferencesDataStoreFactory
import kotlinx.coroutines.CoroutineDispatcher

class LocalStorageFactory(context: Context, private val ioDispatcher: CoroutineDispatcher) {

    private val applicationContext = context.applicationContext

    private val dataStoreFactory = PreferencesDataStoreFactory(applicationContext, ioDispatcher)

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
            fileCacheMetadataMapper = FileCacheMetadataMapperImpl(),
            coroutineDispatcher = ioDispatcher
        )

    fun createPreferencesDataSource(): PreferencesDataSource =
        PreferencesDataSourceImpl(dataStoreFactory, ioDispatcher)

    private companion object {
        const val FILE_CACHE_DATABASE_NAME = "file_cache_metadata"
    }

}
