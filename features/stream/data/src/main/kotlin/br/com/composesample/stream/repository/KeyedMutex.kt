package br.com.composesample.stream.repository

import kotlinx.coroutines.sync.Mutex

internal class KeyedMutex<K> {

    private val registryLock = Any()
    private val entries = mutableMapOf<K, Entry>()

    suspend fun <T> withLock(key: K, action: suspend () -> T): T {
        val entry =
            synchronized(registryLock) {
                entries.getOrPut(key) { Entry() }.also { it.users++ }
            }
        var acquired = false
        try {
            entry.mutex.lock()
            acquired = true
            return action()
        } finally {
            if (acquired) entry.mutex.unlock()
            synchronized(registryLock) {
                entry.users--
                if (entry.users == 0 && entries[key] === entry) entries.remove(key)
            }
        }
    }

    private class Entry {

        val mutex = Mutex()
        var users = 0

    }
}
