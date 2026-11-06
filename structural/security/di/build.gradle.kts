import br.com.composesample.modules.Modules

plugins { alias(libs.plugins.sample.android.library) }

dependencies {
    implementation(project(Modules.Structural.DI.API))
    implementation(libs.coroutines)
    implementation(project(Modules.Structural.Security.API))
    implementation(project(Modules.Structural.Security.IMPL))
    implementation(project(Modules.Structural.Core.MAIN))
}
