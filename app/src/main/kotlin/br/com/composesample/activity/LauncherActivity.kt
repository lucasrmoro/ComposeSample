package br.com.composesample.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import br.com.composesample.navigation.ComposeSampleApp
import br.com.composesample.structural.di.get
import br.com.composesample.structural.navigation.core.NavigationRegistry

class LauncherActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val navigationRegistry = get<NavigationRegistry>()
        setContent { ComposeSampleApp(navigationRegistry) }
    }

}
