package br.com.composesample.structural.local_storage.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import androidx.datastore.tink.AeadSerializer
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.io.File

internal class PreferencesDataStoreFactory(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val applicationContext = context.applicationContext

    val dataStore: DataStore<Preferences> by lazy { createEncryptedDataStore() }

    private fun createEncryptedDataStore(): DataStore<Preferences> {
        val file = File(applicationContext.noBackupFilesDir, PREFERENCES_FILE_NAME)
        val serializer = AeadSerializer(
            aead = createAead(),
            wrappedSerializer = PreferencesFileSerializer,
            associatedData = file.name.toByteArray(Charsets.UTF_8)
        )

        return PreferenceDataStoreFactory.create(
            storage = FileStorage(serializer = serializer, produceFile = { file }),
            scope = CoroutineScope(ioDispatcher + SupervisorJob())
        )
    }

    private fun createAead(): Aead {
        AeadConfig.register()
        val keysetManager = AndroidKeysetManager.Builder()
            .withSharedPref(applicationContext, KEYSET_NAME, KEYSET_PREFERENCES_NAME)
            .withKeyTemplate(KeyTemplate.createFrom(PredefinedAeadParameters.AES256_GCM))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()

        check(keysetManager.isUsingKeystore) { "Android Keystore is required for encrypted preferences" }

        return keysetManager.keysetHandle.getPrimitive(
            RegistryConfiguration.get(),
            Aead::class.java
        )
    }

    private companion object {

        const val PREFERENCES_FILE_NAME = "sample_encrypted.preferences_pb"
        const val KEYSET_NAME = "preferences_keyset"
        const val KEYSET_PREFERENCES_NAME = "sample_datastore_keyset"
        const val MASTER_KEY_URI = "android-keystore://sample_datastore_master_key"

    }

}
