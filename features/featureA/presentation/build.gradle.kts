import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.presentation)
}

dependencies {
    implementation(project(Modules.Structural.Navigation.API))
    implementation(project(Modules.Feature.FeatureA.ANALYTICS))
    implementation(project(Modules.Feature.FeatureA.DOMAIN_API))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
}
