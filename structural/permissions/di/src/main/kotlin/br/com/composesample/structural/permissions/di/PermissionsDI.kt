package br.com.composesample.structural.permissions.di

import androidx.activity.ComponentActivity
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.permissions.PermissionQualifiers
import br.com.composesample.structural.permissions.PermissionsProvider
import br.com.composesample.structural.permissions.createPermissionsProvider

object PermissionsModule {

    private val permissionsModule = dependencyModule {
        singleton<PermissionsProvider>(qualifier = PermissionQualifiers.ApplicationContext) {
            createPermissionsProvider(context = get())
        }
        factory<PermissionsProvider> { params ->
            createPermissionsProvider(activity = params.get<ComponentActivity>())
        }
    }

    fun init() {
        loadModules(permissionsModule)
    }

}
