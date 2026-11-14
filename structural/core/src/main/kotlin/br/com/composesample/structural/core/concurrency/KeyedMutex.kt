package br.com.composesample.structural.core.concurrency

import kotlinx.coroutines.sync.Mutex

/**
 * Serializes suspending actions for each key while allowing actions for different keys to run
 * concurrently. A key's lock is removed after its last active or waiting action completes.
 */
class KeyedMutex<K> {

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
