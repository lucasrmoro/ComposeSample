import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.data)
}

dependencies {
    implementation(project(Modules.Feature.Stream.DOMAIN_API))
    implementation(libs.bundles.room)
}
