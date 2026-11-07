package br.com.composesample.stream.analytics

import br.com.composesample.structural.analytics.AnalyticsEvent
import br.com.composesample.structural.analytics.AnalyticsProvider

internal class StreamAnalyticsImpl(private val provider: AnalyticsProvider) : StreamAnalytics {

    override fun listLoadStarted() = provider.track(AnalyticsEvent("stream_list_load_started"))

    override fun listLoadFinished(itemCount: Int) =
        provider.track(
            AnalyticsEvent("stream_list_load_finished", "item_count" to itemCount.toString())
        )

}

fun createStreamAnalytics(provider: AnalyticsProvider): StreamAnalytics =
    StreamAnalyticsImpl(provider)
