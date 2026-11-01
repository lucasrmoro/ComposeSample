package br.com.composesample.structural.push_notifications

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

fun initializePushNotifications(context: Context) {
    val appContext = context.applicationContext
    val firebaseApp = FirebaseApp.getApps(appContext).firstOrNull {
        it.name == FirebaseApp.DEFAULT_APP_NAME
    } ?: return
    FirebaseMessaging.getInstance().isAutoInitEnabled = true
}
