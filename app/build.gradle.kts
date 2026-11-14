import br.com.composesample.ProjectConfig
import br.com.composesample.ext.stringBuildConfigField
import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.app)
    alias(libs.plugins.compose.compiler)
}

val tmdbReadAccessToken = gradle.extra["tmdbReadAccessToken"] as String

android {
    defaultConfig {
        applicationId = ProjectConfig.APP_ID
        targetSdk = ProjectConfig.TARGET_SDK
        versionCode = ProjectConfig.VERSION_CODE
        versionName = ProjectConfig.VERSION_NAME
        stringBuildConfigField(name = "TMDB_READ_ACCESS_TOKEN", value = tmdbReadAccessToken)
    }
}

dependencies {
    implementation(project(Modules.Structural.Analytics.IMPL))
    implementation(project(Modules.Structural.Analytics.DI))
    implementation(project(Modules.Structural.AssetsCache.DI))
    implementation(project(Modules.Structural.Biometric.API))
    implementation(project(Modules.Structural.Biometric.DI))
    implementation(project(Modules.Structural.Camera.DI))
    implementation(project(Modules.Structural.Core.MAIN))
    implementation(project(Modules.Structural.Core.DI))
    implementation(project(Modules.Structural.DI.API))
    implementation(project(Modules.Structural.DI.IMPL))
    implementation(project(Modules.Structural.DesignSystem.MAIN))
    implementation(project(Modules.Structural.DesignSystem.DI))
    implementation(project(Modules.Structural.LocalStorage.API))
    implementation(project(Modules.Structural.LocalStorage.DI))
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Localization.DI))
    implementation(project(Modules.Structural.Logger.API))
    implementation(project(Modules.Structural.Logger.DI))
    implementation(project(Modules.Structural.Media.DI))
    implementation(project(Modules.Structural.MediaCompression.DI))
    implementation(project(Modules.Structural.Navigation.API))
    implementation(project(Modules.Structural.Navigation.DI))
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Structural.Network.DI))
    implementation(project(Modules.Structural.Notifications.DI))
    implementation(project(Modules.Structural.Permissions.API))
    implementation(project(Modules.Structural.Permissions.DI))
    implementation(project(Modules.Structural.PushNotifications.API))
    implementation(project(Modules.Structural.PushNotifications.DI))
    implementation(project(Modules.Structural.PushNotifications.IMPL))
    implementation(project(Modules.Structural.RemoteConfig.DI))
    implementation(project(Modules.Structural.RemoteConfig.IMPL))
    implementation(project(Modules.Structural.Security.DI))
    implementation(project(Modules.Structural.Session.API))
    implementation(project(Modules.Structural.Session.DI))
    implementation(project(Modules.Structural.VideoPlayer.API))
    implementation(project(Modules.Structural.VideoPlayer.DI))
    implementation(project(Modules.Feature.Login.DI))
    implementation(project(Modules.Feature.Stream.DI))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material)
    testImplementation(libs.androidx.junit)
}
