package br.com.composesample.structural.network.di

import br.com.composesample.structural.di.DependencyQualifier

object AuthenticatedHttpClient : DependencyQualifier {

    override val value: String = "AuthenticatedHttpClient"

}

object UnauthenticatedHttpClient : DependencyQualifier {

    override val value: String = "UnauthenticatedHttpClient"

}
