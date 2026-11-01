import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.DesignSystem.MAIN))
    implementation(project(Modules.Structural.Biometric.API))
    implementation(libs.androidx.appcompat)
    implementation(project(Modules.Structural.DI.API))
}
