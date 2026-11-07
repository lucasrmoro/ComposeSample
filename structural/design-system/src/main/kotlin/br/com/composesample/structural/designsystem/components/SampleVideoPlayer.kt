package br.com.composesample.structural.designsystem.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.tokens.SampleDimens
import br.com.composesample.structural.di.get
import br.com.composesample.structural.video_player.*
import kotlinx.coroutines.flow.catch

@Composable
fun SampleVideoPlayer(modifier: Modifier = Modifier, source: VideoSource, online: Boolean) {
    var playbackState by remember(source) {
        mutableStateOf<VideoPlaybackState>(VideoPlaybackState.Ready)
    }
    val provider = remember { get<VideoPlaybackProvider>() }

    LaunchedEffect(source, online, playbackState) {
        if (online && playbackState == VideoPlaybackState.Loading) {
            provider.prepare(source)
                .catch { playbackState = VideoPlaybackState.Failed }
                .collect { playbackState = VideoPlaybackState.Playing(it) }
        }
    }

    Column(modifier) {
        when {
            !online -> Text(stringResource(R.string.sample_video_offline))
            playbackState == VideoPlaybackState.Failed ->
                Text(stringResource(R.string.sample_video_error))
            playbackState is VideoPlaybackState.Playing ->
                VideoWebView(
                    embed = (playbackState as VideoPlaybackState.Playing).embed,
                    onPause = { playbackState = VideoPlaybackState.Ready }
                )
            else -> Button(onClick = { playbackState = VideoPlaybackState.Loading }) {
                Text(stringResource(R.string.sample_play_trailer))
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun VideoWebView(embed: VideoEmbed, onPause: () -> Unit) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                (webView?.webChromeClient as? VideoFullscreenChromeClient)?.close()
                webView?.onPause()
                webView?.loadUrl(BLANK_PAGE)
                onPause()
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    AndroidView(
        modifier = Modifier.fillMaxWidth().aspectRatio(SampleDimens.Ratio.Video),
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
                    embed.baseUrl,
                    embed.html,
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

private const val BLANK_PAGE = "about:blank"
private const val HTML_MIME_TYPE = "text/html"
