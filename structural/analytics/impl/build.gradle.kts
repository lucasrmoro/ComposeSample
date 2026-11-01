import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Analytics.API))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}
