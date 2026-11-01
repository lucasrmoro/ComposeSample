package br.com.composesample.structural.local_storage.prefs

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> EncryptedPreferenceType<T>.toDataStoreKey(): Preferences.Key<T> = when (this) {
    is EncryptedPreferenceType.StringValue -> stringPreferencesKey(key)
    is EncryptedPreferenceType.BooleanValue -> booleanPreferencesKey(key)
    is EncryptedPreferenceType.IntValue -> intPreferencesKey(key)
    is EncryptedPreferenceType.LongValue -> longPreferencesKey(key)
    is EncryptedPreferenceType.FloatValue -> floatPreferencesKey(key)
    is EncryptedPreferenceType.DoubleValue -> doublePreferencesKey(key)
    is EncryptedPreferenceType.StringSetValue -> stringSetPreferencesKey(key)
    is EncryptedPreferenceType.ByteArrayValue -> byteArrayPreferencesKey(key)
} as Preferences.Key<T>
