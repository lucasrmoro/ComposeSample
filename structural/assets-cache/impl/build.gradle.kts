import br.com.composesample.modules.Modules

plugins { alias(libs.plugins.sample.android.library) }

dependencies {
    implementation(project(Modules.Structural.LocalStorage.API))
    implementation(project(Modules.Structural.Security.API))
    implementation(project(Modules.Structural.DI.API))
    implementation(libs.coroutines)
    implementation(project(Modules.Structural.AssetsCache.API))
    implementation(project(Modules.Structural.Media.API))
    implementation(project(Modules.Structural.Network.API))
}
