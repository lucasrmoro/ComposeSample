import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {

    implementation(project(Modules.Structural.RemoteConfig.API))
    implementation(project(Modules.Structural.RemoteConfig.IMPL))
    implementation(project(Modules.Structural.DI.API))
}
