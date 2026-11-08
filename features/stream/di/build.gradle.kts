import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.di)
}

dependencies {
    implementation(project(Modules.Feature.Stream.DATA))
}
