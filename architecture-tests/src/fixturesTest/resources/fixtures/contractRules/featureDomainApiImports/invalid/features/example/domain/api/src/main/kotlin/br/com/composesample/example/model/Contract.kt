package br.com.composesample.example.model

import kotlinx.coroutines.Job

interface Contract {
    fun work(): Job
}
