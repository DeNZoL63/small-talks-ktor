package ru.ordertime.small_talks.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ordertime.small_talks.client.sendMessage
import ru.ordertime.small_talks.client.spaceClient
import ru.ordertime.small_talks.command.getSupportedCommands
import ru.ordertime.small_talks.command.runHelpCommand
import ru.ordertime.small_talks.command.supportedCommands
import ru.ordertime.small_talks.domain.UserProfile
import space.jetbrains.api.runtime.helpers.*
import space.jetbrains.api.runtime.types.*

fun Application.configureRouting() {

    routing {
        post("api/space") {
            // read request body
            val body = call.receiveText()

            // verify if the request comes from a trusted Space instance
            val signature = call.request.header("X-Space-Public-Key-Signature")
            val timestamp = call.request.header("X-Space-Timestamp")?.toLongOrNull()

            // verifyWithPublicKey gets a key from Space, uses it to generate message hash
            // and compares the generated hash to the hash in a message
            if (signature.isNullOrBlank() || timestamp == null ||
                !spaceClient.verifyWithPublicKey(body, timestamp, signature)
            ) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            when (val payload = readPayload(body)) {
                is ListCommandsPayload -> {
                    // Space requests the list of supported commands
                    call.respondText(
                        // JSON serializer
                        ObjectMapper().writeValueAsString(getSupportedCommands()),
                        ContentType.Application.Json
                    )
                }

                is MessagePayload -> {
                    // user sent a message to the application
                    val commandName = payload.command()
                    val command = supportedCommands.find { it.name == commandName }
                    if (command == null) {
                        runHelpCommand(payload)
                    } else {
                        launch { command.run(payload) }
                    }
                    call.respond(HttpStatusCode.OK, "")
                }
            }
        }

        post("api/space/greetings") {
            val body = call.receiveText()

            // verify if the request comes from a trusted Space instance
            val signature = call.request.header("X-Space-Public-Key-Signature")
            val timestamp = call.request.header("X-Space-Timestamp")?.toLongOrNull()

            // verifyWithPublicKey gets a key from Space, uses it to generate message hash
            // and compares the generated hash to the hash in a message
            if (signature.isNullOrBlank() || timestamp == null ||
                !spaceClient.verifyWithPublicKey(body, timestamp, signature)
            ) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            transaction {
                val payload = readPayload(body)
                val newUser = UserProfile.new {
                    clientId = payload.clientId!!
                    spaceUserId = ((payload as WebhookRequestPayload).payload as ProfileOrganizationEvent).member.id
                    subscribed = false
                }
                runBlocking {
                    sendMessage(newUser.spaceUserId, greetNewUser())
                }
            }
        }
    }
}

private fun greetNewUser(): ChatMessage {
    return message {
        section {
            text(
                "Glad to see you here! Be my guest and write me"
            )
        }
    }
}