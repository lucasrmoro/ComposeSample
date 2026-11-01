package br.com.composesample.structural.core.di

import br.com.composesample.structural.di.DependencyQualifier

object DispatcherIO : DependencyQualifier {

    override val value: String = "DispatcherIO"

}

object DispatcherMain : DependencyQualifier {

    override val value: String = "DispatcherMain"

}

object DispatcherDefault : DependencyQualifier {

    override val value: String = "DispatcherDefault"

}
