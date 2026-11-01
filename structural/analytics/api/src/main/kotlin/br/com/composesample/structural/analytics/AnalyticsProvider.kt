package br.com.composesample.structural.analytics

/** Logs events without exposing the analytics SDK to consuming layers. */
interface AnalyticsProvider {

    /** Returns ACCEPTED when the call is forwarded to the SDK, without guaranteeing delivery. */
    fun track(event: AnalyticsEvent): AnalyticsResult

}
