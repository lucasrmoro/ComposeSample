package br.com.composesample.structural.network

/** Refreshes an expired access token using the application's authentication provider. */
fun interface AccessTokenRefresher {

    /** Returns a fresh access token, or `null` when refreshing fails. */
    suspend fun refreshAccessToken(refreshToken: String): String?

}
