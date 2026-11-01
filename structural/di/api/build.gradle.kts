plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    api(libs.androidx.lifecycle.viewmodel.savedstate)
    api(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
}
