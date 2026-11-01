package br.com.composesample.structural.designsystem.biometric

import br.com.composesample.structural.biometric.BiometricProvider
import kotlinx.coroutines.CoroutineScope

fun createLifecycleBiometricProvider(delegate: BiometricProvider, screenScope: CoroutineScope): BiometricProvider =
    LifecycleBiometricProvider(delegate, screenScope)
