package br.com.composesample.structural.logger

import timber.log.Timber

internal class LoggerImpl : Logger {

    private val tree = Timber.DebugTree()

    override fun debug(message: String) {
        tree.d(message)
    }

    override fun error(throwable: Throwable) {
        tree.e(throwable)
    }

}
