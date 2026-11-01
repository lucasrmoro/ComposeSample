package br.com.composesample.structural.core.ext

import br.com.composesample.structural.core.di.DispatcherDefault
import br.com.composesample.structural.core.di.DispatcherIO
import br.com.composesample.structural.core.di.DispatcherMain
import kotlinx.coroutines.CoroutineDispatcher
import br.com.composesample.structural.di.DependencyResolver
import br.com.composesample.structural.di.get

val DependencyResolver.dispatcherIO: CoroutineDispatcher
    get() = get(DispatcherIO)

val DependencyResolver.dispatcherMain: CoroutineDispatcher
    get() = get(DispatcherMain)

val DependencyResolver.dispatcherDefault: CoroutineDispatcher
    get() = get(DispatcherDefault)
