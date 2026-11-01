import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(project(Modules.Structural.Biometric.API))
    implementation(project(Modules.Structural.DI.API))
    implementation(project(Modules.Structural.Core.MAIN))
}
