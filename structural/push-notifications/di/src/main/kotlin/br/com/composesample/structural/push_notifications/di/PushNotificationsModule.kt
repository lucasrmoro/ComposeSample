package br.com.composesample.structural.push_notifications.di

import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.push_notifications.PushMessageHandler
import br.com.composesample.structural.push_notifications.PushTokenProvider
import br.com.composesample.structural.push_notifications.createPushMessageHandler
import br.com.composesample.structural.push_notifications.createPushTokenProvider

object PushNotificationsModule {

    private val pushNotificationsModule = dependencyModule {
        singleton<PushTokenProvider> { createPushTokenProvider(context = get()) }
        singleton<PushMessageHandler> { createPushMessageHandler(notificationProvider = get()) }
    }

    fun init() {
        loadModules(pushNotificationsModule)
    }

}
