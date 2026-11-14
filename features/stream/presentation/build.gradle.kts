import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.presentation)
}

dependencies {
    implementation(project(Modules.Structural.AssetsCache.API))
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Structural.Navigation.API))
    implementation(project(Modules.Structural.VideoPlayer.API))
    implementation(project(Modules.Feature.Stream.ANALYTICS))
    implementation(project(Modules.Feature.Stream.DOMAIN_API))
}
