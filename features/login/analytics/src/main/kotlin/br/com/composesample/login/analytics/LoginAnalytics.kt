package br.com.composesample.login.analytics

import br.com.composesample.structural.analytics.AnalyticsResult

interface LoginAnalytics {

    fun tokenRefreshStarted(): AnalyticsResult

    fun tokenRefreshFinished(succeeded: Boolean): AnalyticsResult

}
