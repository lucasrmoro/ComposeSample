package br.com.composesample.structural.network.http

import okhttp3.HttpUrl

internal fun HttpRequest.resolvePath(): String {
    require(
        path.isNotBlank() && !path.startsWith("/") && ':' !in path && '?' !in path && '#' !in path
    ) {
        "Use a relative path and supply query parameters separately"
    }
    var resolved = path
    pathParameters.forEach { (name, value) ->
        val placeholder = "{$name}"
        require(placeholder in resolved) { "Unknown path parameter: $name" }
        require(value != "." && value != "..") { "Path parameters cannot be dot segments" }
        val encoded = HttpUrl.Builder().scheme("https").host("localhost")
            .addPathSegment(value).build().encodedPath.removePrefix("/")
        resolved = resolved.replace(placeholder, encoded)
    }
    require('{' !in resolved && '}' !in resolved) { "Missing path parameters" }
    return resolved
}
