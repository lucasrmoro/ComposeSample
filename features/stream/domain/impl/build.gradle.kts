import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.domain)
}

dependencies {
    implementation(libs.coroutines)
    implementation(project(Modules.Feature.Stream.DOMAIN_API))
}
