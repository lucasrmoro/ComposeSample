import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.domain)
}

dependencies {
    implementation(project(Modules.Feature.FeatureA.DOMAIN_API))
}