package br.com.composesample.structural.notifications

/** Contains the user-visible content needed to display a notification. */
data class NotificationRequest(
    val id: Int,
    val channelId: String,
    val channelName: String,
    val channelDescription: String,
    val title: String,
    val body: String
)
