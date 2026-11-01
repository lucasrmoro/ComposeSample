import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Navigation.API))
    implementation(project(Modules.Structural.Navigation.IMPL))
    implementation(project(Modules.Structural.DI.API))
}
