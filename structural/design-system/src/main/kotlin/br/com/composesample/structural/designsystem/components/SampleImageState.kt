package br.com.composesample.structural.designsystem.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import br.com.composesample.structural.assets_cache.AssetRequest
import br.com.composesample.structural.assets_cache.AssetsCacheProvider
import br.com.composesample.structural.core.di.DispatcherIO
import br.com.composesample.structural.di.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

internal data class SampleImageState(val bitmap: ImageBitmap?, val isUnavailable: Boolean)

@Composable
internal fun rememberSampleImageState(request: AssetRequest?): SampleImageState {
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
        val provider = cache ?: return@LaunchedEffect
        provider.observe(request, previousUrl).onEach { state ->
            failed = state.failed
            state.file?.let { file ->
                val decoded = withContext(requireNotNull(dispatcher)) {
                    file.openStream().use(BitmapFactory::decodeStream)?.asImageBitmap()
                }
                if (decoded == null) {
                    failed = true
                } else {
                    bitmap = decoded
                    previousUrl = request.url
                }
            }
        }.catch { failed = true }.collect()
    }
    return SampleImageState(bitmap = bitmap, isUnavailable = failed || request == null)
}
