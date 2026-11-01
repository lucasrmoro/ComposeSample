package br.com.composesample.structural.push_notifications

/** Contains user-visible content and data from an incoming push message. */
data class PushMessage(
    val title: String?,
    val body: String?,
    val data: Map<String, String>
)
