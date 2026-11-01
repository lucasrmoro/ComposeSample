package br.com.composesample.featureA.dto

import br.com.composesample.structural.network.http.JsonName

data class SomeDTO(
    @JsonName("serverId") val id: Long?,
    @JsonName("textId") val text: String?
)