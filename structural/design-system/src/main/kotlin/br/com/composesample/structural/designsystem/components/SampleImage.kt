package br.com.composesample.structural.designsystem.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import br.com.composesample.structural.core.di.DispatcherIO
import br.com.composesample.structural.design_system.R
import br.com.composesample.structural.di.get
import br.com.composesample.structural.assets_cache.AssetsCacheProvider
import br.com.composesample.structural.assets_cache.AssetRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/** Displays a locally cached image; decoding and network work stay off the main dispatcher. */
@Composable
fun SampleImage(
    request: AssetRequest?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val inspection = LocalInspectionMode.current
    val cache = if (inspection) null else remember { get<AssetsCacheProvider>() }
    val dispatcher = if (inspection) null else remember { get<CoroutineDispatcher>(DispatcherIO) }
    var previousUrl by remember { mutableStateOf<String?>(null) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var failed by remember(request) { mutableStateOf(false) }
    LaunchedEffect(request, cache) {
        if (request == null) {
            bitmap = null
            return@LaunchedEffect
        }
        cache
            ?.observe(request, previousUrl)
            ?.onEach { state ->
                failed = state.failed
                state.file?.let { file ->
                    val decoded =
                        withContext(requireNotNull(dispatcher)) {
                            file.openStream().use(BitmapFactory::decodeStream)?.asImageBitmap()
                        }
                    decoded?.let {
                        bitmap = it
                        previousUrl = request.url
                    } ?: run { failed = true }
                }
            }
            ?.catch { failed = true }
            ?.collect()
    }
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val image = bitmap
        image?.let {
            Image(it, contentDescription, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
            ?: Text(
                stringResource(
                    if (failed || request == null) R.string.sample_image_unavailable
                    else R.string.sample_image_loading
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
    }
}
