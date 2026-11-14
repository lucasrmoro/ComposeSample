import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.di)
}

dependencies {
    testImplementation(project(Modules.Structural.Core.DI))
    testImplementation(project(Modules.Structural.DI.IMPL))
    testImplementation(project(Modules.Structural.Localization.API))
    testImplementation(project(Modules.Structural.Localization.DI))
    implementation(project(Modules.Feature.Login.ANALYTICS))
    implementation(project(Modules.Feature.Login.DOMAIN_API))
    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(project(Modules.Structural.Network.DI))
    implementation(project(Modules.Feature.Login.DATA))
}
