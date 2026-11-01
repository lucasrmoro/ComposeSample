package br.com.composesample.structural.analytics

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

fun createAnalyticsProvider(context: Context): AnalyticsProvider {
    val appContext = context.applicationContext
    val initialized = FirebaseApp.getApps(appContext).any { it.name == FirebaseApp.DEFAULT_APP_NAME }
    val analytics = if (initialized) FirebaseAnalytics.getInstance(appContext) else null
    return AnalyticsProviderImpl(analytics)
}
