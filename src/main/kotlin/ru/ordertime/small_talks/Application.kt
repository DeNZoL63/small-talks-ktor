package ru.ordertime.small_talks

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.ordertime.small_talks.database.DatabaseFactory
import ru.ordertime.small_talks.plugins.configureRouting
import ru.ordertime.small_talks.service.initManager

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureRouting()
    initManager()
}
