import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(Modules.Structural.Session.API))
    implementation(project(Modules.Feature.Login.DOMAIN_API))
    implementation(project(Modules.Structural.Core.MAIN))
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Structural.LocalStorage.API))
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.okhttp.client)
    debugImplementation(libs.okhttp.loggingInterceptor)
    implementation(libs.coroutines)
}