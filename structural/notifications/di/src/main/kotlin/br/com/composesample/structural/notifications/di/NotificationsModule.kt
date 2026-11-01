package br.com.composesample.structural.notifications.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.notifications.NotificationProvider
import br.com.composesample.structural.notifications.createNotificationProvider
import br.com.composesample.structural.permissions.PermissionQualifiers
import br.com.composesample.structural.permissions.PermissionsProvider

object NotificationsModule {

    private val notificationsModule = dependencyModule {
        singleton<NotificationProvider> {
            createNotificationProvider(
                context = get(),
                permissionsProvider = get<PermissionsProvider>(qualifier = PermissionQualifiers.ApplicationContext)
            )
        }
    }

    fun init() {
        loadModules(notificationsModule)
    }

}
