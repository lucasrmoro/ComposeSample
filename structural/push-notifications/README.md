# Push Notifications

Encapsulates Firebase Cloud Messaging token access and incoming message handling.
Consumers depend on `push-notifications/api`; Firebase SDK types remain in
`impl`. The `push-notifications/di` module registers `PushTokenProvider` as a
singleton. `AppApplication` initializes Firebase explicitly before calling
`initializePushNotifications(this)` and loads `PushNotificationsModule.init()`
with the other structural modules.

## Usage

Resolve `PushTokenProvider` and call `getToken()` from a coroutine. It returns
`null` when the default Firebase app is unavailable; Firebase task failures are
propagated to the caller.

```kotlin
val token = pushTokenProvider.getToken()
```

The implementation declares `PushMessagingService` in its manifest. In the
foreground, the service resolves `PushMessageHandler` from the app's dependency
container through `PushMessagingServiceHost`, maps messages to `PushMessage`,
and displays messages that provide a title and body through the local notification provider. The values can
come from the notification payload or the `title` and `body` data keys. Optional
`channel_id`, `channel_name`, and `channel_description` data keys customize the
channel, while integer `notification_id` selects the notification identifier.
The default channel is created as needed by `NotificationProvider.show()`.

When the app is in the background, FCM notification payloads are displayed by
Android and do not invoke `onMessageReceived`; this prevents duplicate display.
Data-only messages reach the service, but background display requires a title
and body in the data payload. Callers remain responsible for requesting Android
13+ notification permission before expecting user-visible notifications.

## Configure Firebase

Add a valid `google-services.json` for the app variants and apply the Google
Services Gradle plugin in the app as described in the [Analytics configuration
guide](../analytics/README.md#configure-firebase-later). The Firebase BoM and
Messaging dependency are declared only in `impl`. Without Firebase configuration,
the app starts normally and `getToken()` returns `null`.

FCM requires a trusted server or Firebase environment to send messages; this
service only implements the Android client. Do not send FCM tokens to untrusted
destinations.

References: [FCM Android setup](https://firebase.google.com/docs/cloud-messaging/android/get-started)
and [receiving messages](https://firebase.google.com/docs/cloud-messaging/android/receive-messages).
