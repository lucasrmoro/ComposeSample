package br.com.composesample.structural.network.http

suspend inline fun <reified T> HttpClient.get(request: HttpRequest): T =
    get(request, object : HttpResponseType<T>() {})

suspend inline fun <reified T> HttpClient.post(request: HttpRequest, body: Any? = null): T =
    post(request, body, object : HttpResponseType<T>() {})

suspend inline fun <reified T> HttpClient.put(request: HttpRequest, body: Any? = null): T =
    put(request, body, object : HttpResponseType<T>() {})

suspend inline fun <reified T> HttpClient.delete(request: HttpRequest, body: Any? = null): T =
    delete(request, body, object : HttpResponseType<T>() {})
