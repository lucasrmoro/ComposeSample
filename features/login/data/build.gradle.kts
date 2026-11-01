import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.data)
}

dependencies {
    implementation(project(Modules.Feature.Login.ANALYTICS))
    implementation(project(Modules.Feature.Login.DOMAIN_API))
}
