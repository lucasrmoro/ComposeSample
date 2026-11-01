package br.com.composesample.featureA.analytics

import br.com.composesample.structural.analytics.AnalyticsEvent
import br.com.composesample.structural.analytics.AnalyticsProvider
import br.com.composesample.structural.analytics.AnalyticsResult

internal class FeatureAAnalyticsImpl(private val provider: AnalyticsProvider) : FeatureAAnalytics {

    override fun listLoadStarted(page: Int): AnalyticsResult =
        provider.track(AnalyticsEvent(
            "feature_a_list_load_started",
            "page" to page.toString()
        ))

    override fun listLoadFinished(page: Int, itemCount: Int): AnalyticsResult =
        provider.track(AnalyticsEvent(
            "feature_a_list_load_finished",
            "page" to page.toString(),
            "item_count" to itemCount.toString()
        ))

}

fun createFeatureAAnalytics(provider: AnalyticsProvider): FeatureAAnalytics = FeatureAAnalyticsImpl(provider)
