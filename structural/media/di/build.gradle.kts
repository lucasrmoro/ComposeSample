import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Media.API))
    implementation(project(Modules.Structural.Media.IMPL))
    implementation(project(Modules.Structural.DI.API))
    implementation(project(Modules.Structural.Permissions.API))
}
