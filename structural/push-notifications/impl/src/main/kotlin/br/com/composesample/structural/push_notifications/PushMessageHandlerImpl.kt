package br.com.composesample.structural.push_notifications

import br.com.composesample.structural.notifications.NotificationProvider
import br.com.composesample.structural.notifications.NotificationRequest

internal class PushMessageHandlerImpl(
    private val notificationProvider: NotificationProvider
) : PushMessageHandler {

    override fun handle(message: PushMessage) {
        val title = message.title ?: message.data["title"] ?: return
        val body = message.body ?: message.data["body"] ?: return
        val channelId = message.data["channel_id"] ?: DEFAULT_CHANNEL_ID
        notificationProvider.show(
            NotificationRequest(
                id = message.data["notification_id"]?.toIntOrNull() ?: message.hashCode(),
                channelId = channelId,
                channelName = message.data["channel_name"] ?: DEFAULT_CHANNEL_NAME,
                channelDescription = message.data["channel_description"] ?: DEFAULT_CHANNEL_DESCRIPTION,
                title = title,
                body = body
            )
        )
    }

    private companion object {
        const val DEFAULT_CHANNEL_ID = "push_notifications"
        const val DEFAULT_CHANNEL_NAME = "Push notifications"
        const val DEFAULT_CHANNEL_DESCRIPTION = "Notifications received from push messages"
    }

}
