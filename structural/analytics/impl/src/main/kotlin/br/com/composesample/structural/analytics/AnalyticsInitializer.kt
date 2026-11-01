package br.com.composesample.structural.analytics

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

fun initializeFirebase(context: Context) {
    FirebaseApp.initializeApp(context.applicationContext)
}

fun initializeAnalytics(context: Context) {
    val appContext = context.applicationContext
    if (FirebaseApp.getApps(appContext).any { it.name == FirebaseApp.DEFAULT_APP_NAME }) {
        FirebaseAnalytics.getInstance(appContext)
    }
}
