package br.com.composesample.structural.push_notifications

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

internal class PushTokenProviderImpl(private val messaging: FirebaseMessaging?) : PushTokenProvider {

    override suspend fun getToken(): String? = messaging?.token?.await()

}
