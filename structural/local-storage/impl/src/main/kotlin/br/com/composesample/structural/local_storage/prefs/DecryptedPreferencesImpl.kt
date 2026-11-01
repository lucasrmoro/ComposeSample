package br.com.composesample.structural.local_storage.prefs

import androidx.datastore.preferences.core.Preferences

internal class DecryptedPreferencesImpl(values: Preferences) : DecryptedPreferences {

    private var values: Preferences? = values

    override fun <T : Any> get(preference: EncryptedPreferenceType<T>): T? {
        val preferences = checkNotNull(values) { "Decrypted preferences have already been closed" }
        return preferences[preference.toDataStoreKey()]
    }

    override fun close() {
        values = null
    }

}
