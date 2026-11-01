package br.com.composesample.structural.push_notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        (application as PushMessagingServiceHost).resolveLogger().debug("FCM token refreshed")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        (application as PushMessagingServiceHost).resolvePushMessageHandler().handle(
            PushMessage(
                title = message.notification?.title,
                body = message.notification?.body,
                data = message.data
            )
        )
    }

}
