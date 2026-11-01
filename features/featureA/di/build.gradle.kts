import br.com.composesample.modules.Modules
import br.com.composesample.modules.Modules.Feature.FeatureA

plugins {
    alias(libs.plugins.sample.di)
}

dependencies {
    implementation(project(FeatureA.ANALYTICS))
    implementation(project(Modules.Structural.Navigation.API))
    implementation(project(FeatureA.DATA))
    implementation(project(FeatureA.DOMAIN_API))
    implementation(project(FeatureA.DOMAIN_IMPL))
    implementation(project(FeatureA.PRESENTATION))
}