import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Camera.API))
    implementation(project(Modules.Structural.Camera.IMPL))
    implementation(project(Modules.Structural.DI.API))
    implementation(project(Modules.Structural.Media.API))
}
