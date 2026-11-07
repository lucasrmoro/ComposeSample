package br.com.composesample.stream.analytics

import br.com.composesample.structural.analytics.AnalyticsResult

interface StreamAnalytics {

    fun listLoadStarted(): AnalyticsResult

    fun listLoadFinished(itemCount: Int): AnalyticsResult

}
