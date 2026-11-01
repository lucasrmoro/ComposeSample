package br.com.composesample.structural.local_storage.prefs

/** Values scoped to the caller. Close after use to release the retained references. */
interface DecryptedPreferences : AutoCloseable {

    /**
     * Returns the value identified by [preference], or null when it does not exist.
     * Keep each key associated with the same native type used when saving it.
     *
     * @throws IllegalStateException If this result has already been closed.
     */
    operator fun <T : Any> get(preference: EncryptedPreferenceType<T>): T?

    /** Releases retained references. Does not erase values already retained by the caller. */
    override fun close()

}
