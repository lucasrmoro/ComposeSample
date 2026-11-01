package br.com.composesample.structural.remote_config

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson

fun createRemoteConfigProvider(context: Context): RemoteConfigProvider {
    val firebaseApp = FirebaseApp.getApps(context.applicationContext).firstOrNull {
        it.name == FirebaseApp.DEFAULT_APP_NAME
    }
    return RemoteConfigProviderImpl(
        remoteConfig = firebaseApp?.let(FirebaseRemoteConfig::getInstance),
        gson = Gson()
    )
}
