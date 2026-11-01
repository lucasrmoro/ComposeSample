import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.MediaCompression.API))
    implementation(project(Modules.Structural.Media.API))
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.media3.transformer)
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines)
}
