package br.com.composesample.structural.media

import kotlinx.coroutines.flow.Flow

interface Contract {
    fun work(): Flow<String>
}
