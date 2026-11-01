import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Logger.API))
    implementation(libs.gson)

    implementation(project(Modules.Structural.RemoteConfig.API))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.coroutinesPlayServices)
}
