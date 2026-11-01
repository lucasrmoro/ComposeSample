package br.com.composesample.login.model

import br.com.composesample.structural.network.http.JsonName

data class LoginDTO(
    @JsonName("AccessToken") val accessToken: String?,
    @JsonName("RefreshToken") val refreshToken: String?
)