package br.com.composesample.structural.network.http

import okhttp3.OkHttpClient

internal fun OkHttpClient.Builder.withHttpLogging(): OkHttpClient.Builder = this
