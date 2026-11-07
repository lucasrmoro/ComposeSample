package br.com.composesample.structural.designsystem.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.onFocusedBoundsChanged
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalViewConfiguration

/** Clears focus on taps outside the focused child without consuming child gestures. */
@Composable
fun ClearFocusOnOutsideTapSample(content: @Composable () -> Unit) {
    val focusManager = LocalFocusManager.current
    val viewConfiguration = LocalViewConfiguration.current
    val bounds = remember { FocusBounds() }
    Box(
        modifier =
            Modifier.fillMaxSize()
                .onGloballyPositioned { bounds.root = it }
                .onFocusedBoundsChanged { bounds.focused = it }
                .pointerInput(focusManager, viewConfiguration) {
                    awaitEachGesture {
                        val down =
                            awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial
                            )
                        var isTap = true
                        var position = down.position
                        do {
                            val event = awaitPointerEvent(pass = PointerEventPass.Final)
                            event.changes
                                .firstOrNull { it.id == down.id }
                                ?.let { change ->
                                    position = change.position
                                    if (
                                        (position - down.position).getDistance() >
                                            viewConfiguration.touchSlop
                                    )
                                        isTap = false
                                }
                            if (event.changes.size > 1) isTap = false
                        } while (event.changes.any { it.pressed })
                        if (isTap) {
                            val root = bounds.root
                            val focused = bounds.focused
                            if (root?.isAttached == true && focused?.isAttached == true) {
                                if (!focused.boundsInRoot().contains(root.localToRoot(position)))
                                    focusManager.clearFocus()
                            }
                        }
                    }
                }
    ) {
        content()
    }
}

private class FocusBounds {

    var root: LayoutCoordinates? = null

    var focused: LayoutCoordinates? = null

}
