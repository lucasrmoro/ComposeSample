import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.data)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(Modules.Structural.Network.API))
    implementation(project(Modules.Feature.Stream.DOMAIN_API))
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
}
