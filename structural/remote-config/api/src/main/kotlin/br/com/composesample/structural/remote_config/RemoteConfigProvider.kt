package br.com.composesample.structural.remote_config

import android.os.Parcelable

/**
 * Reads active remote configuration without network requests.
 * Missing values, invalid conversions, and unconfigured Firebase use the supplied defaults.
 * Fetching and activation are managed internally at application startup.
 */
interface RemoteConfigProvider {

    fun getString(key: String, defaultValue: String = ""): String

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    fun getInt(key: String, defaultValue: Int = 0): Int

    fun getLong(key: String, defaultValue: Long = 0L): Long

    fun getFloat(key: String, defaultValue: Float = 0f): Float

    fun getDouble(key: String, defaultValue: Double = 0.0): Double

    /**
     * Decodes a remote JSON string into [type], falling back on missing or malformed values.
     * Parcelable marks the model type; Parcel serialization is not used.
     */
    fun <T : Parcelable> getParcelable(key: String, type: Class<T>, defaultValue: T? = null): T?

}
