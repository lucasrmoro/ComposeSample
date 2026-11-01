# Notifications

Encapsulates local Android notification channels and display. Consumers depend
on `notifications/api`; Android notification APIs remain in `impl`. The
`notifications/di` module registers `NotificationProvider` as a singleton, and
`AppApplication` loads `NotificationsModule.init()` with the other structural
modules.

## Usage

The provider creates or updates the channel automatically when `show()` is
called. Android keeps any channel settings already changed by the user.

```kotlin
val result = notificationProvider.show(
    NotificationRequest(
        id = 1,
        channelId = "messages",
        channelName = "Messages",
        channelDescription = "Updates about new messages",
        title = "New message",
        body = "You have a new message."
    )
)
```

`show()` returns `PERMISSION_REQUIRED` on Android 13 and later if the app has not
been granted `POST_NOTIFICATIONS`; the provider checks through the structural
`PermissionsProvider`, while callers own the runtime permission flow. It
returns `DISPLAYED` after handing the notification to Android, which does not
guarantee that the user sees it. The implementation declares the
`POST_NOTIFICATIONS` manifest permission for app merging.

## Dependency injection

`NotificationsModule.init()` registers the provider. Its factory only retains the
application context and starts no background work.
