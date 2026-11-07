import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(project(Modules.Structural.Biometric.API))
    implementation(project(Modules.Structural.Core.MAIN))
    implementation(project(Modules.Structural.DI.API))
    implementation(project(Modules.Structural.VideoPlayer.API))
    implementation(project(Modules.Structural.AssetsCache.API))
    implementation(project(Modules.Structural.Media.API))
    implementation(project(Modules.Structural.Network.API))
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
