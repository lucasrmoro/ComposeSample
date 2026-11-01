package br.com.composesample.structural.local_storage

import android.content.Context
import androidx.room.Room
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

    fun createPreferencesDataSource(): PreferencesDataSource =
        PreferencesDataSourceImpl(dataStoreFactory, ioDispatcher)

    fun someDao(): SomeDao = database.fooDao()

    private companion object {

        const val ROOM_DATABASE_NAME = "sample_data_base"

    }

}
