import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.PushNotifications.API))
    implementation(project(Modules.Structural.Notifications.API))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.coroutinesPlayServices)
    implementation(libs.androidx.core.ktx)
}
