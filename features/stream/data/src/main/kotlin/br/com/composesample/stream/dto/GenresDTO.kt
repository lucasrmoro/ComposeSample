package br.com.composesample.stream.dto

import br.com.composesample.structural.network.http.JsonName

internal data class GenresDTO(@JsonName("genres") val genres: List<GenreDTO> = emptyList())
