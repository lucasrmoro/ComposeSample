package br.com.composesample.structural.designsystem.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.util.Consumer
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

internal class VideoFullscreenChromeClient(
    context: Context
) : WebChromeClient() {

    private val activity = context.findActivity() as? ComponentActivity

    private var fullscreenView: View? = null

    private var fullscreenCallback: CustomViewCallback? = null

    private var backCallback: OnBackPressedCallback? = null

    private var previousOrientation: OrientationBeforeFullscreen? = null

    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        if (fullscreenView != null) {
            callback.onCustomViewHidden()
            return
        }
        val host = activity ?: run {
            callback.onCustomViewHidden()
            return
        }
        attachFullscreenView(host, view, callback)
        registerBackCallback(host)
        enterFullscreen(host)
    }

    private fun attachFullscreenView(
        host: ComponentActivity,
        view: View,
        callback: CustomViewCallback
    ) {
        host.findViewById<ViewGroup>(android.R.id.content).addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        fullscreenView = view
        fullscreenCallback = callback
    }

    private fun registerBackCallback(host: ComponentActivity) {
        val closeOnBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                close()
            }
        }
        backCallback = closeOnBack
        host.onBackPressedDispatcher.addCallback(closeOnBack)
    }

    private fun enterFullscreen(host: ComponentActivity) {
        host.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        WindowInsetsControllerCompat(host.window, host.window.decorView)
            .hide(WindowInsetsCompat.Type.navigationBars())
        previousOrientation = OrientationBeforeFullscreen(
            requested = host.requestedOrientation,
            displayed = host.resources.configuration.orientation
        )
        host.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    override fun onHideCustomView() {
        removeFullscreenView()
    }

    fun close() {
        removeFullscreenView()?.onCustomViewHidden()
    }

    private fun removeFullscreenView(): CustomViewCallback? {
        val view = fullscreenView ?: return null
        fullscreenView = null
        val callback = fullscreenCallback
        fullscreenCallback = null
        backCallback?.remove()
        backCallback = null
        (view.parent as? ViewGroup)?.removeView(view)
        activity?.let {
            it.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            WindowInsetsControllerCompat(it.window, it.window.decorView)
                .show(WindowInsetsCompat.Type.navigationBars())
        }
        restoreOrientation()
        return callback
    }

    private fun restoreOrientation() {
        val previous = previousOrientation ?: return
        previousOrientation = null
        val host = activity ?: return
        if (host.resources.configuration.orientation == previous.displayed) {
            host.requestedOrientation = previous.requested
            return
        }
        val target = when (previous.displayed) {
            Configuration.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> previous.requested
        }
        var listener: Consumer<Configuration>? = null
        listener = Consumer { configuration ->
            if (configuration.orientation == previous.displayed) {
                listener?.let(host::removeOnConfigurationChangedListener)
                host.requestedOrientation = previous.requested
            }
        }
        host.addOnConfigurationChangedListener(listener)
        host.requestedOrientation = target
    }

}

private data class OrientationBeforeFullscreen(
    val requested: Int,
    val displayed: Int
)

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
