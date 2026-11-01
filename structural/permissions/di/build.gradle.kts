import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Permissions.API))
    implementation(project(Modules.Structural.Permissions.IMPL))
    implementation(libs.androidx.activity.ktx)
    implementation(project(Modules.Structural.DI.API))
}
