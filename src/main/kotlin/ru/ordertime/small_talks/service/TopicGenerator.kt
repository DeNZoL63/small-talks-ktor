package ru.ordertime.small_talks.service

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val randomUrlService = "https://www.conversationstarters.com/random.php"
private val client: HttpClient = HttpClient.newHttpClient()
private val request: HttpRequest = HttpRequest.newBuilder()
    .uri(URI.create(randomUrlService))
    .build()

fun generateTopic(): String {
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    return extractTopic(response.body())
}

private fun extractTopic(body: String?): String {
    if (body == null) return ""

    return body.replace(Regex("<.+>"), "")
}