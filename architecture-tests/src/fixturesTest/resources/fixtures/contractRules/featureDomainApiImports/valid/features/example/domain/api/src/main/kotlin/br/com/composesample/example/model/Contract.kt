package br.com.composesample.example.model

import kotlinx.coroutines.flow.Flow

interface Contract {
    fun work(): Flow<String>
}
