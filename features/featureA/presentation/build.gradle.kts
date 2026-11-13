import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.presentation)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(project(Modules.Feature.FeatureA.ANALYTICS))
    implementation(project(Modules.Feature.FeatureA.DOMAIN_API))
    implementation(project(Modules.Structural.Navigation.API))
}
