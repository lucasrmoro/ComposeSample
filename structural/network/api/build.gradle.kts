import br.com.composesample.modules.Modules

plugins {
    alias(libs.plugins.sample.android.library)
}

dependencies {
    api(libs.androidx.lifecycle.livedata.core.ktx)
    api(project(Modules.Structural.DI.API))
    api(libs.coroutines)
}
