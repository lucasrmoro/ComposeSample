package br.com.composesample.structural.biometric

import javax.crypto.Cipher
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation

internal class BiometricAuthenticationRequest(
    continuation: CancellableContinuation<BiometricResult<Cipher>>
) {

    private var continuation: CancellableContinuation<BiometricResult<Cipher>>? = continuation

    private var state = State.PENDING

    val isActive: Boolean
        get() = state == State.PENDING && continuation?.isActive == true

    fun invokeOnCancellation(block: () -> Unit) {
        continuation?.invokeOnCancellation { block() }
    }

    fun cancel(): Boolean {
        if (state != State.PENDING) {
            return false
        }
        state = State.CANCELLING
        resume(BiometricResult.Failure(BiometricError.CANCELLED))
        return true
    }

    fun complete(result: BiometricResult<Cipher>) {
        if (state == State.COMPLETED) {
            return
        }
        val finalResult = if (state == State.CANCELLING) {
            BiometricResult.Failure(BiometricError.CANCELLED)
        } else {
            result
        }
        state = State.COMPLETED
        resume(finalResult)
    }

    private fun resume(result: BiometricResult<Cipher>) {
        val pending = continuation
        continuation = null
        if (pending?.isActive == true) {
            pending.resume(result)
        }
    }

    private enum class State {
        PENDING,
        CANCELLING,
        COMPLETED
    }

}
