import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.presentation)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(project(Modules.Structural.AssetsCache.API))
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Structural.VideoPlayer.API))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(project(Modules.Feature.Stream.ANALYTICS))
    implementation(project(Modules.Feature.Stream.DOMAIN_API))
    implementation(project(Modules.Structural.Navigation.API))
}
