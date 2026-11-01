import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Biometric.API))
    implementation(project(Modules.Structural.Biometric.IMPL))
    implementation(project(Modules.Structural.Core.MAIN))
    implementation(libs.androidx.biometric)
    implementation(project(Modules.Structural.DI.API))
}
