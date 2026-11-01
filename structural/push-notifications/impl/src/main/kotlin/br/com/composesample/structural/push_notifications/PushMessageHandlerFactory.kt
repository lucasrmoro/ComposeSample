package br.com.composesample.structural.push_notifications

import br.com.composesample.structural.notifications.NotificationProvider

fun createPushMessageHandler(notificationProvider: NotificationProvider): PushMessageHandler =
    PushMessageHandlerImpl(notificationProvider)
