package br.com.composesample.structural.navigation.core

import android.net.Uri

/** Encodes an arbitrary label as a single navigation argument. */
fun encodeRouteValue(value: String): String = Uri.encode(value)
