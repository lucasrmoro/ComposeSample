import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    api(project(Modules.Structural.Session.API))
    api(project(Modules.Structural.Network.API))
    implementation(libs.bundles.android)
    api(project(Modules.Structural.DI.API))
}
