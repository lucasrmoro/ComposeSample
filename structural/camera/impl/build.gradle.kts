import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Camera.API))
    implementation(project(Modules.Structural.Media.API))
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
}
