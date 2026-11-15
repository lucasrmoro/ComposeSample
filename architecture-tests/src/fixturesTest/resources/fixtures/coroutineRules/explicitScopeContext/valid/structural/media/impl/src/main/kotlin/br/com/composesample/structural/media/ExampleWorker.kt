package br.com.composesample.structural.media

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ExampleWorker(private val dispatcher: CoroutineDispatcher) {

    val scope = CoroutineScope(dispatcher + SupervisorJob())

}
