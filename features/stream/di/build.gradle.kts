import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.di)
}

dependencies {
    implementation(project(Modules.Feature.Stream.ANALYTICS))
    implementation(project(Modules.Feature.Stream.DATA))
    implementation(project(Modules.Feature.Stream.DOMAIN_API))
    implementation(project(Modules.Feature.Stream.PRESENTATION))
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Network.API))
}
