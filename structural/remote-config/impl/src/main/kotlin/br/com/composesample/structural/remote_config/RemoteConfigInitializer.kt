package br.com.composesample.structural.remote_config

import android.content.Context
import br.com.composesample.structural.logger.Logger
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

suspend fun initializeRemoteConfig(context: Context, logger: Logger) {
    val firebaseApp = FirebaseApp.getApps(context.applicationContext).firstOrNull {
        it.name == FirebaseApp.DEFAULT_APP_NAME
    } ?: return
    val remoteConfig = FirebaseRemoteConfig.getInstance(firebaseApp)
    try {
        remoteConfig.activate().await()
        remoteConfig.fetch().await()
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        logger.error(exception)
    }
}
