package br.com.composesample.stream.analytics

interface StreamAnalytics {
    fun listLoadStarted()
    fun listLoadFinished(itemCount: Int)
}
