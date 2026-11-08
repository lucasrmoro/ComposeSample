package br.com.composesample.structural.designsystem.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.designsystem.theme.CurrentTheme
import br.com.composesample.structural.di.get
import br.com.composesample.structural.video_player.VideoEmbed
import br.com.composesample.structural.video_player.VideoPlaybackProvider
import br.com.composesample.structural.video_player.VideoSource
import kotlinx.coroutines.flow.catch

private const val BLANK_PAGE = "about:blank"
private const val HTML_MIME_TYPE = "text/html"

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

    Column(modifier = modifier) {
        when {
            !online -> SampleText(text = stringResource(R.string.sample_video_offline))
            playbackState == VideoPlaybackState.Failed ->
                SampleText(text = stringResource(R.string.sample_video_error))
            playbackState is VideoPlaybackState.Playing ->
                VideoWebView(
                    embed = (playbackState as VideoPlaybackState.Playing).embed,
                    onPause = { playbackState = VideoPlaybackState.Ready }
                )
            else -> SampleButton(
                text = stringResource(R.string.sample_play_trailer),
                onClick = { playbackState = VideoPlaybackState.Loading }
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun VideoWebView(
    modifier: Modifier = Modifier,
    embed: VideoEmbed,
    onPause: () -> Unit
) {
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
        modifier = modifier.fillMaxWidth().aspectRatio(CurrentTheme.ratio.video),
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
