package br.com.composesample.structural.designsystem.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.util.Consumer
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.tokens.Dimen
import br.com.composesample.structural.di.get
import br.com.composesample.structural.video_player.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoPlayerSample(source: VideoSource, online: Boolean, modifier: Modifier = Modifier) {
    var requested by rememberSaveable(source) { mutableStateOf(false) }
    var embed by remember(source) { mutableStateOf<VideoEmbed?>(null) }
    var failed by remember(source) { mutableStateOf(false) }
    val provider = remember { get<VideoPlaybackProvider>() }
    LaunchedEffect(requested, source, online) {
        if (requested && online) {
            provider
                .prepare(source)
                .onEach {
                    embed = it
                    failed = false
                }
                .catch { failed = true }
                .collect()
        }
    }
    Column(modifier) {
        if (!online) Text(stringResource(R.string.sample_video_offline))
        else if (failed) Text(stringResource(R.string.sample_video_error))
        else if (embed == null)
            Button(onClick = { requested = true }) {
                Text(stringResource(R.string.sample_play_trailer))
            }
        else {
            val document = requireNotNull(embed)
            var webView by remember { mutableStateOf<WebView?>(null) }
            val lifecycle = LocalLifecycleOwner.current.lifecycle
            DisposableEffect(lifecycle) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_PAUSE) {
                        (webView?.webChromeClient as? VideoFullscreenChromeClient)?.close()
                        webView?.onPause()
                        webView?.loadUrl(BLANK_PAGE)
                        embed = null
                        requested = false
                    }
                }
                lifecycle.addObserver(observer)
                onDispose { lifecycle.removeObserver(observer) }
            }
            AndroidView(
                modifier = Modifier.fillMaxWidth().aspectRatio(Dimen.Ratio.Video),
                factory = { context ->
                    WebView(context).apply {
                        webView = this
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = false
                        settings.allowContentAccess = false
                        settings.mediaPlaybackRequiresUserGesture = true
                        webViewClient = WebViewClient()
                        webChromeClient = VideoFullscreenChromeClient(context)
                        loadDataWithBaseURL(
                            document.baseUrl,
                            document.html,
                            HTML_MIME_TYPE,
                            Charsets.UTF_8.name(),
                            null
                        )
                    }
                },
                onRelease = { view ->
                    (view.webChromeClient as? VideoFullscreenChromeClient)?.close()
                    view.stopLoading()
                    view.loadUrl(BLANK_PAGE)
                    view.removeAllViews()
                    view.destroy()
                    webView = null
                }
            )
        }
    }
}

private const val BLANK_PAGE = "about:blank"
private const val HTML_MIME_TYPE = "text/html"

private class VideoFullscreenChromeClient(
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
        val content = host.findViewById<ViewGroup>(android.R.id.content)
        content.addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        fullscreenView = view
        fullscreenCallback = callback
        val closeOnBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                close()
            }
        }
        backCallback = closeOnBack
        host.onBackPressedDispatcher.addCallback(closeOnBack)
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
