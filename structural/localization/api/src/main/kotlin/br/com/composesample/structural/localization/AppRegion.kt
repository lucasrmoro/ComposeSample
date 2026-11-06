package br.com.composesample.structural.localization

/** Effective application language and device regional settings. */
data class AppRegion(val languageTag: String, val countryCode: String, val timeZoneId: String)
