import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Structural.Network.IMPL))
    implementation(project(Modules.Structural.Core.MAIN))
    implementation(project(Modules.Structural.Session.API))
    implementation(project(Modules.Structural.LocalStorage.API))
    implementation(project(Modules.Feature.Login.DOMAIN_API))
    implementation(project(Modules.Structural.DI.API))
    implementation(libs.okhttp.client)
}
