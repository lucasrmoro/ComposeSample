import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.di)
}

dependencies {
    implementation(project(Modules.Structural.Analytics.API))
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Navigation.API))
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Feature.Stream.ANALYTICS))
    implementation(project(Modules.Feature.Stream.DATA))
    implementation(project(Modules.Feature.Stream.DOMAIN_API))
    implementation(project(Modules.Feature.Stream.DOMAIN_IMPL))
    implementation(project(Modules.Feature.Stream.PRESENTATION))
}
