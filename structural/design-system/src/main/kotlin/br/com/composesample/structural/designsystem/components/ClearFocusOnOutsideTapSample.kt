package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.onFocusedBoundsChanged
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalViewConfiguration

/** Clears focus when a tap lands outside the focused element. */
@Composable
fun ClearFocusOnOutsideTapSample(content: @Composable () -> Unit) {
    val focusManager = LocalFocusManager.current
    val viewConfiguration = LocalViewConfiguration.current
    val focusedBounds = remember { mutableStateOf<Rect?>(null) }

    Box(
        modifier =
            Modifier.fillMaxSize()
                .onFocusedBoundsChanged { focusedBounds.value = it?.boundsInRoot() }
                .pointerInput(focusManager, viewConfiguration) {
                    awaitEachGesture {
                        val down =
                            awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial
                            )
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Final)
                        if (
                            up != null &&
                                (up.position - down.position).getDistance() <=
                                    viewConfiguration.touchSlop &&
                                focusedBounds.value?.contains(up.position) == false
                        ) {
                            focusManager.clearFocus()
                        }
                    }
                }
    ) {
        content()
    }
}
