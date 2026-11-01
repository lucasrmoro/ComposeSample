package br.com.composesample.structural.notifications

/** Describes a notification channel created for Android 8.0 and later. */
data class NotificationChannelSpec(
    val id: String,
    val name: String,
    val description: String
)
