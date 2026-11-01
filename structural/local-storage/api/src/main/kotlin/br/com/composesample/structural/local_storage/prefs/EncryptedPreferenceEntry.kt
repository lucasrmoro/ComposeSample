package br.com.composesample.structural.local_storage.prefs

class EncryptedPreferenceEntry<T : Any> internal constructor(
    val preference: EncryptedPreferenceType<T>,
    val value: T
)
