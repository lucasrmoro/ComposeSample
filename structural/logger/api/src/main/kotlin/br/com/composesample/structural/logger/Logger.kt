package br.com.composesample.structural.logger

interface Logger {

    fun debug(message: String)

    fun error(throwable: Throwable)

}
