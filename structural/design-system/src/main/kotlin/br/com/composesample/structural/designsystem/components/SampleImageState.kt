package br.com.composesample.structural.designsystem.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import br.com.composesample.structural.assets_cache.AssetRequest
import br.com.composesample.structural.assets_cache.AssetState
import br.com.composesample.structural.assets_cache.AssetsCacheProvider
import br.com.composesample.structural.core.di.DispatcherIO
import br.com.composesample.structural.di.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal sealed interface SampleImageState {
    data object Loading : SampleImageState
    data class Loaded(val bitmap: ImageBitmap) : SampleImageState
    data object Unavailable : SampleImageState
}

@Composable
internal fun rememberSampleImageState(request: AssetRequest?): SampleImageState {
    if (LocalInspectionMode.current) {
        return if (request == null) SampleImageState.Unavailable else SampleImageState.Loading
    }
    val cache = remember { get<AssetsCacheProvider>() }
    val dispatcher = remember { get<CoroutineDispatcher>(DispatcherIO) }
    var previousUrl by remember { mutableStateOf<String?>(null) }
    val state by produceState<SampleImageState>(SampleImageState.Loading, request, cache, dispatcher) {
        if (request == null) {
            value = SampleImageState.Unavailable
            return@produceState
        }
        if (value !is SampleImageState.Loaded) value = SampleImageState.Loading
        cache.observe(request, previousUrl).map { it.toImageState(dispatcher) }
            .catch { emit(SampleImageState.Unavailable) }.collect { updated ->
                if (updated is SampleImageState.Loaded) previousUrl = request.url
                if (value !is SampleImageState.Loaded || updated is SampleImageState.Loaded) value = updated
            }
    }
    return state
}

private suspend fun AssetState.toImageState(dispatcher: CoroutineDispatcher): SampleImageState {
    val source = file ?: return if (failed) SampleImageState.Unavailable else SampleImageState.Loading
    val bitmap = withContext(dispatcher) {
        source.openStream().use(BitmapFactory::decodeStream)?.asImageBitmap()
    }
    if (bitmap == null) return SampleImageState.Unavailable
    return SampleImageState.Loaded(bitmap)
}
