package br.com.composesample.structural.network.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

internal fun OkHttpClient.Builder.withHttpLogging(): OkHttpClient.Builder =
    addInterceptor(
        HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            level = HttpLoggingInterceptor.Level.BODY
        }
    )
