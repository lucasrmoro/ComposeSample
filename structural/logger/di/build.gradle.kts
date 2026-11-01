import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Logger.API))
    implementation(project(Modules.Structural.Logger.IMPL))
    implementation(project(Modules.Structural.DI.API))
}
