package br.com.composesample.structural.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

internal class AnalyticsProviderImpl(private val analytics: FirebaseAnalytics?) : AnalyticsProvider {

    override fun track(event: AnalyticsEvent): AnalyticsResult {
        val delegate = analytics ?: return AnalyticsResult.UNAVAILABLE
        val parameters = Bundle().apply {
            event.parameters.forEach { (name, value) ->
                putString(name, value)
            }
        }
        delegate.logEvent(event.name, parameters)
        return AnalyticsResult.ACCEPTED
    }

}
