import br.com.composesample.ext.stringBuildConfigField
import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.app)
    alias(libs.plugins.compose.compiler)
}

val tmdbReadAccessToken = gradle.extra["tmdbReadAccessToken"] as String

android {
    defaultConfig {
        stringBuildConfigField(name = "TMDB_READ_ACCESS_TOKEN", value = tmdbReadAccessToken)
    }
}

dependencies {
    implementation(project(Modules.Structural.Analytics.IMPL))
    implementation(project(Modules.Structural.AssetsCache.DI))
    implementation(project(Modules.Structural.Camera.DI))
    implementation(project(Modules.Structural.DI.IMPL))
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Localization.DI))
    implementation(project(Modules.Structural.Media.DI))
    implementation(project(Modules.Structural.MediaCompression.DI))
    implementation(project(Modules.Structural.Navigation.API))
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Structural.PushNotifications.API))
    implementation(project(Modules.Structural.PushNotifications.IMPL))
    implementation(project(Modules.Structural.RemoteConfig.IMPL))
    implementation(project(Modules.Structural.Security.DI))
    implementation(project(Modules.Structural.VideoPlayer.API))
    implementation(project(Modules.Structural.VideoPlayer.DI))
    implementation(project(Modules.Feature.Login.DI))
    implementation(project(Modules.Feature.Stream.DI))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
}
