import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Analytics.API))
    implementation(project(Modules.Structural.Analytics.IMPL))
    implementation(project(Modules.Structural.DI.API))
}
