package br.com.composesample.structural.network.ext

import br.com.composesample.structural.di.DependencyResolver
import br.com.composesample.structural.di.get
import br.com.composesample.structural.network.di.AuthenticatedHttpClient
import br.com.composesample.structural.network.di.UnauthenticatedHttpClient
import br.com.composesample.structural.network.http.HttpClient

val DependencyResolver.httpClient: HttpClient
    get() = get(AuthenticatedHttpClient)

val DependencyResolver.httpClientNoAuth: HttpClient
    get() = get(UnauthenticatedHttpClient)
