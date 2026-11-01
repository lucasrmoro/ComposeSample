import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    implementation(project(Modules.Structural.Core.MAIN))
    implementation(project(Modules.Structural.DI.API))
}
