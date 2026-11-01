package br.com.composesample.structural.remote_config

import android.os.Parcelable
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.gson.Gson
import com.google.gson.JsonParseException

internal class RemoteConfigProviderImpl(
    private val remoteConfig: FirebaseRemoteConfig?,
    private val gson: Gson
) : RemoteConfigProvider {

    override fun getString(key: String, defaultValue: String): String =
        readValue(key, defaultValue) { it.asString() }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        readValue(key, defaultValue) { it.asBoolean() }

    override fun getInt(key: String, defaultValue: Int): Int =
        readValue(key, defaultValue) { it.asString().toInt() }

    override fun getLong(key: String, defaultValue: Long): Long =
        readValue(key, defaultValue) { it.asLong() }

    override fun getFloat(key: String, defaultValue: Float): Float =
        readValue(key, defaultValue) { it.asString().toFloat().takeIf(Float::isFinite) ?: defaultValue }

    override fun getDouble(key: String, defaultValue: Double): Double =
        readValue(key, defaultValue) { it.asDouble().takeIf(Double::isFinite) ?: defaultValue }

    override fun <T : Parcelable> getParcelable(key: String, type: Class<T>, defaultValue: T?): T? {
        val json = getRemoteValue(key)?.asString() ?: return defaultValue
        return try {
            gson.fromJson(json, type) ?: defaultValue
        } catch (_: JsonParseException) {
            defaultValue
        }
    }

    private fun <T> readValue(
        key: String,
        defaultValue: T,
        read: (FirebaseRemoteConfigValue) -> T
    ): T {
        val value = getRemoteValue(key) ?: return defaultValue
        return try {
            read(value)
        } catch (_: IllegalArgumentException) {
            defaultValue
        }
    }

    private fun getRemoteValue(key: String): FirebaseRemoteConfigValue? {
        require(key.isNotBlank()) { "Remote config key must not be blank" }
        return remoteConfig?.getValue(key)?.takeIf {
            it.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE
        }
    }

}
