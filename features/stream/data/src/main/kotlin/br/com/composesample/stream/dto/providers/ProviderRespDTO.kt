package br.com.composesample.stream.dto.providers

import br.com.composesample.structural.network.http.JsonName

data class ProviderRespDTO(
    @JsonName("provider_id") val id: Int = 0,
    @JsonName("provider_name") val name: String = "",
    @JsonName("logo_path") val logoPath: String? = null
)
