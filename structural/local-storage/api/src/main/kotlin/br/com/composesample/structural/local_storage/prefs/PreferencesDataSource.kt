package br.com.composesample.structural.local_storage.prefs

import br.com.composesample.structural.biometric.BiometricEncryption
import br.com.composesample.structural.biometric.BiometricResult

/**
 * Stores native preferences in the shared encrypted DataStore.
 * Biometric packages add a single-use biometric encryption layer to that same storage.
 * Storage IO errors propagate to the caller.
 */
interface PreferencesDataSource {

    /**
     * Reads the encrypted package at [key] once, without requesting biometrics or exposing plaintext.
     *
     * @return A snapshot to authenticate and decrypt, or null when the key does not exist.
     * @throws IllegalArgumentException If [key] is blank.
     */
    suspend fun getEncrypted(key: String): EncryptedPreferences?

    /**
     * Encrypts all [entries] in one operation and replaces the complete package at [key].
     * Other storage keys are preserved; entries omitted from this package are not merged from the old value.
     *
     * @param encryption An unconsumed authorization returned by biometric enrollment.
     * @return Success after persistence, or the serialization/encryption failure that prevented writing.
     * @throws IllegalArgumentException If the storage key is blank, entries are empty, or entry keys repeat.
     * @throws IllegalStateException If [encryption] has already been consumed.
     */
    suspend fun putEncrypted(
        key: String,
        encryption: BiometricEncryption,
        vararg entries: EncryptedPreferenceEntry<*>
    ): BiometricResult<Unit>

    suspend fun getString(key: String, defValue: String? = null): String?

    suspend fun putString(key: String, value: String?)

    suspend fun getBoolean(key: String, defValue: Boolean): Boolean

    suspend fun putBoolean(key: String, value: Boolean)

    suspend fun getInt(key: String, defValue: Int): Int

    suspend fun putInt(key: String, value: Int)

    suspend fun getLong(key: String, defValue: Long): Long

    suspend fun putLong(key: String, value: Long)

    companion object {

        const val ACCESS_TOKEN = "ACCESS_TOKEN"

        const val REFRESH_TOKEN = "REFRESH_TOKEN"

    }

}
