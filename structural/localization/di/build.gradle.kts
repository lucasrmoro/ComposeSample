import br.com.composesample.modules.Modules

plugins { alias(libs.plugins.sample.android.library) }

dependencies {
    implementation(project(Modules.Structural.DI.API))
    implementation(libs.coroutines)
    implementation(project(Modules.Structural.Localization.API))
    implementation(project(Modules.Structural.Localization.IMPL))
    implementation(project(Modules.Structural.Core.MAIN))
}
