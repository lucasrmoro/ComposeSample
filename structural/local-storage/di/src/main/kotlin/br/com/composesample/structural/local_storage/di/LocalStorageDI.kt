package br.com.composesample.structural.local_storage.di

import br.com.composesample.featureA.dao.SomeDao
import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.local_storage.LocalStorageFactory
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource

object LocalStorageModule {

    private val localStorageModule = dependencyModule {
        singleton { LocalStorageFactory(context = get(), ioDispatcher = dispatcherIO) }
        singleton<PreferencesDataSource> { get<LocalStorageFactory>().createPreferencesDataSource() }
        factory<SomeDao> { get<LocalStorageFactory>().someDao() }
    }

    fun init() {
        loadModules(localStorageModule)
    }

}
