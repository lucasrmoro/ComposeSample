package br.com.composesample.structural.local_storage.prefs

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.composesample.structural.biometric.BiometricDecryption
import br.com.composesample.structural.biometric.BiometricEncryption
import br.com.composesample.structural.biometric.BiometricEncryptedData
import br.com.composesample.structural.biometric.BiometricError
import br.com.composesample.structural.biometric.BiometricResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException

internal class PreferencesDataSourceImpl(
    private val dataStoreFactory: PreferencesDataStoreFactory,
    private val ioDispatcher: CoroutineDispatcher
) : PreferencesDataSource {

    private val encryptedCodec = EncryptedPreferencesCodec()

    override suspend fun getEncrypted(key: String): EncryptedPreferences? {
        require(key.isNotBlank()) { "Encrypted preference key must not be blank" }
        return getString(key)?.let { value ->
            EncryptedPreferencesImpl(BiometricEncryptedData(value))
        }
    }

    override suspend fun putEncrypted(
        key: String,
        encryption: BiometricEncryption,
        vararg entries: EncryptedPreferenceEntry<*>
    ): BiometricResult<Unit> = withContext(ioDispatcher) {
        require(key.isNotBlank()) { "Encrypted preference key must not be blank" }
        require(entries.isNotEmpty()) { "Provide at least one preference to encrypt" }
        require(entries.map { it.preference.key }.distinct().size == entries.size) {
            "Preference keys must be unique within the batch"
        }
        val serialized = try {
            encryptedCodec.encode(entries)
        } catch (_: IOException) {
            return@withContext BiometricResult.Failure(BiometricError.ENCRYPTION_FAILED)
        }
        val result = try {
            encryption.encrypt(serialized)
        } finally {
            serialized.fill(0)
        }
        when (result) {
            is BiometricResult.Failure -> result
            is BiometricResult.Success -> {
                putString(key, result.data.value)
                BiometricResult.Success(Unit)
            }
        }
    }

    override suspend fun getString(key: String, defValue: String?): String? = withContext(ioDispatcher) {
        dataStoreFactory.dataStore.data.first()[stringPreferencesKey(key)] ?: defValue
    }

    override suspend fun putString(key: String, value: String?) {
        withContext(ioDispatcher) {
            val preferenceKey = stringPreferencesKey(key)
            dataStoreFactory.dataStore.edit {
                if (value == null) it.remove(preferenceKey) else it[preferenceKey] = value
            }
        }
    }

    override suspend fun getBoolean(key: String, defValue: Boolean): Boolean = withContext(ioDispatcher) {
        dataStoreFactory.dataStore.data.first()[booleanPreferencesKey(key)] ?: defValue
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        withContext(ioDispatcher) {
            dataStoreFactory.dataStore.edit { it[booleanPreferencesKey(key)] = value }
        }
    }

    override suspend fun getInt(key: String, defValue: Int): Int = withContext(ioDispatcher) {
        dataStoreFactory.dataStore.data.first()[intPreferencesKey(key)] ?: defValue
    }

    override suspend fun putInt(key: String, value: Int) {
        withContext(ioDispatcher) {
            dataStoreFactory.dataStore.edit { it[intPreferencesKey(key)] = value }
        }
    }

    override suspend fun getLong(key: String, defValue: Long): Long = withContext(ioDispatcher) {
        dataStoreFactory.dataStore.data.first()[longPreferencesKey(key)] ?: defValue
    }

    override suspend fun putLong(key: String, value: Long) {
        withContext(ioDispatcher) {
            dataStoreFactory.dataStore.edit { it[longPreferencesKey(key)] = value }
        }
    }

    private inner class EncryptedPreferencesImpl(
        override val data: BiometricEncryptedData
    ) : EncryptedPreferences {

        override suspend fun decrypt(
            decryption: BiometricDecryption
        ): BiometricResult<DecryptedPreferences> = withContext(ioDispatcher) {
            when (val result = decryption.decrypt(data)) {
                is BiometricResult.Failure -> result
                is BiometricResult.Success -> decode(result.data)
            }
        }

        private suspend fun decode(bytes: ByteArray): BiometricResult<DecryptedPreferences> = try {
            BiometricResult.Success(DecryptedPreferencesImpl(encryptedCodec.decode(bytes)))
        } catch (_: IOException) {
            BiometricResult.Failure(BiometricError.DECRYPTION_FAILED)
        } finally {
            bytes.fill(0)
        }

    }

}
