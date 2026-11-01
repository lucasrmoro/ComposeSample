package br.com.composesample.structural.local_storage.prefs

/** Associates a storage field name with one supported native preference type. */
sealed class EncryptedPreferenceType<T : Any>(val key: String) {

    init {
        require(key.isNotBlank()) { "Encrypted preference key must not be blank" }
    }

    fun withValue(value: T): EncryptedPreferenceEntry<T> = EncryptedPreferenceEntry(this, value)

    class StringValue internal constructor(key: String) : EncryptedPreferenceType<String>(key)

    class BooleanValue internal constructor(key: String) : EncryptedPreferenceType<Boolean>(key)

    class IntValue internal constructor(key: String) : EncryptedPreferenceType<Int>(key)

    class LongValue internal constructor(key: String) : EncryptedPreferenceType<Long>(key)

    class FloatValue internal constructor(key: String) : EncryptedPreferenceType<Float>(key)

    class DoubleValue internal constructor(key: String) : EncryptedPreferenceType<Double>(key)

    class StringSetValue internal constructor(key: String) : EncryptedPreferenceType<Set<String>>(key)

    class ByteArrayValue internal constructor(key: String) : EncryptedPreferenceType<ByteArray>(key)

    companion object {

        fun string(key: String): EncryptedPreferenceType<String> = StringValue(key)

        fun boolean(key: String): EncryptedPreferenceType<Boolean> = BooleanValue(key)

        fun int(key: String): EncryptedPreferenceType<Int> = IntValue(key)

        fun long(key: String): EncryptedPreferenceType<Long> = LongValue(key)

        fun float(key: String): EncryptedPreferenceType<Float> = FloatValue(key)

        fun double(key: String): EncryptedPreferenceType<Double> = DoubleValue(key)

        fun stringSet(key: String): EncryptedPreferenceType<Set<String>> = StringSetValue(key)

        fun byteArray(key: String): EncryptedPreferenceType<ByteArray> = ByteArrayValue(key)

    }

}
