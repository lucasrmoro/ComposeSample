package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

data class GenresRespDTO(@JsonName("genres") val genres: List<GenreRespDTO> = emptyList())
