package br.com.composesample.stream.ui

import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.structural.localization.AppRegion

internal fun AppRegion.toCatalogRegion() =
    CatalogRegion(languageTag = languageTag, countryCode = countryCode, timeZoneId = timeZoneId)
