import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    api(project(Modules.Structural.Localization.API))
    api(libs.coroutines)
}
