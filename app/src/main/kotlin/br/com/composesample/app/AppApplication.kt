package br.com.composesample.app

import android.app.Application
import br.com.composesample.configuration.registerLocalizationSettings
import br.com.composesample.featureA.FeatureAModule
import br.com.composesample.login.LoginModule
import br.com.composesample.structural.analytics.di.AnalyticsModule
import br.com.composesample.structural.analytics.initializeAnalytics
import br.com.composesample.structural.analytics.initializeFirebase
import br.com.composesample.structural.biometric.di.BiometricModule
import br.com.composesample.structural.camera.di.CameraModule
import br.com.composesample.structural.media.di.MediaModule
import br.com.composesample.structural.media_compression.di.MediaCompressionModule
import br.com.composesample.structural.core.di.CoreModule
import br.com.composesample.structural.core.di.DispatcherIO
import br.com.composesample.structural.designsystem.di.DesignSystemModule
import br.com.composesample.structural.di.DependencyResolver
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.startDependencyInjection
import br.com.composesample.structural.local_storage.di.LocalStorageModule
import br.com.composesample.structural.localization.AppRegionProvider
import br.com.composesample.structural.localization.di.LocalizationModule
import br.com.composesample.structural.logger.di.LoggerModule
import br.com.composesample.structural.logger.Logger
import br.com.composesample.structural.navigation.di.NavigationModule
import br.com.composesample.structural.network.di.NetworkModule
import br.com.composesample.structural.notifications.di.NotificationsModule
import br.com.composesample.structural.permissions.di.PermissionsModule
import br.com.composesample.structural.push_notifications.PushMessageHandler
import br.com.composesample.structural.push_notifications.PushMessagingServiceHost
import br.com.composesample.structural.push_notifications.di.PushNotificationsModule
import br.com.composesample.structural.push_notifications.initializePushNotifications
import br.com.composesample.structural.remote_config.di.RemoteConfigModule
import br.com.composesample.structural.remote_config.initializeRemoteConfig
import br.com.composesample.structural.session.di.SessionModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppApplication : Application(), PushMessagingServiceHost {

    private lateinit var dependencyResolver: DependencyResolver

    override fun onCreate() {
        super.onCreate()
        initializeFirebase(this)
        initializeAnalytics(this)
        initializePushNotifications(this)
        dependencyResolver = setupDependencyInjection()
        dependencyResolver.get<AppRegionProvider>().start()
        val applicationScope =
            CoroutineScope(
                SupervisorJob() + dependencyResolver.get<CoroutineDispatcher>(DispatcherIO)
            )
        applicationScope.launch {
            initializeRemoteConfig(context = this@AppApplication, logger = dependencyResolver.get())
        }
    }

    private fun setupDependencyInjection(): DependencyResolver =
        startDependencyInjection(this) {
            setupStructuralModules()
            setupFeatureModules()
        }

    private fun setupStructuralModules() {
        registerLocalizationSettings()
        NavigationModule.init()
        LoggerModule.init()
        CoreModule.init()
        AnalyticsModule.init()
        RemoteConfigModule.init()
        NotificationsModule.init()
        PushNotificationsModule.init()
        SessionModule.init()
        PermissionsModule.init()
        BiometricModule.init()
        MediaModule.init()
        CameraModule.init()
        MediaCompressionModule.init()
        DesignSystemModule.init()
        LocalizationModule.init()
        NetworkModule.init()
        LocalStorageModule.init()
    }

    private fun setupFeatureModules() {
        LoginModule.init()
        FeatureAModule.init(navigationRegistry = get())
    }

    override fun resolvePushMessageHandler(): PushMessageHandler = dependencyResolver.get()

    override fun resolveLogger(): Logger = dependencyResolver.get()

}
