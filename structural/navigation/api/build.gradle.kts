plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.animation)
    api(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
}
