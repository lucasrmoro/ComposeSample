package br.com.composesample.structural.navigation.core

import java.net.URLEncoder

/** Encodes an arbitrary label as a single navigation argument. */
fun encodeRouteValue(value: String): String = URLEncoder.encode(value, "UTF-8").replace("+", "%20")
