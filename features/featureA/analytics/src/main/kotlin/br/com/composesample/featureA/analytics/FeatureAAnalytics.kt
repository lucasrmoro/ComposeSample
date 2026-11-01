package br.com.composesample.featureA.analytics

import br.com.composesample.structural.analytics.AnalyticsResult

interface FeatureAAnalytics {

    fun listLoadStarted(page: Int): AnalyticsResult

    fun listLoadFinished(page: Int, itemCount: Int): AnalyticsResult

}
