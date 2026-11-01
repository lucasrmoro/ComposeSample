import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(Modules.Structural.Core.MAIN))
    implementation(project(Modules.Feature.FeatureA.DATA))
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(project(Modules.Structural.LocalStorage.API))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.tink)
    implementation(libs.tink.android)
}
