package br.com.composesample.structural.media

import kotlinx.coroutines.Job

interface Contract {
    fun work(): Job
}
