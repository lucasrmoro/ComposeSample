package br.com.composesample.structural.local_storage.prefs

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import androidx.datastore.preferences.core.mutablePreferencesOf
import java.io.ByteArrayOutputStream

internal class EncryptedPreferencesCodec {

    suspend fun encode(entries: Array<out EncryptedPreferenceEntry<*>>): ByteArray {
        val preferences = mutablePreferencesOf()
        entries.forEach { put(preferences, it) }
        return ByteArrayOutputStream().use { output ->
            PreferencesFileSerializer.writeTo(preferences, output)
            output.toByteArray()
        }
    }

    suspend fun decode(data: ByteArray): Preferences = data.inputStream().use { input ->
        PreferencesFileSerializer.readFrom(input)
    }

    private fun <T : Any> put(preferences: MutablePreferences, entry: EncryptedPreferenceEntry<T>) {
        preferences[entry.preference.toDataStoreKey()] = entry.value
    }

}
