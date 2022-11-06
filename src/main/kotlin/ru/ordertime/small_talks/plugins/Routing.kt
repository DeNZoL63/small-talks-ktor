package ru.ordertime.small_talks.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import ru.ordertime.small_talks.client.AppInstanceStorage
import ru.ordertime.small_talks.client.KtorRequestAdapter
import ru.ordertime.small_talks.client.ktorClient
import ru.ordertime.small_talks.client.requestPermissions
import ru.ordertime.small_talks.command.getSupportedCommands
import ru.ordertime.small_talks.command.runHelpCommand
import ru.ordertime.small_talks.command.supportedCommands
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.Space
import space.jetbrains.api.runtime.helpers.SpaceHttpResponse
import space.jetbrains.api.runtime.helpers.command
import space.jetbrains.api.runtime.helpers.processPayload
import space.jetbrains.api.runtime.types.InitPayload
import space.jetbrains.api.runtime.types.ListCommandsPayload
import space.jetbrains.api.runtime.types.MessagePayload

@ExperimentalSpaceSdkApi
fun Application.configureRouting() {

    routing {
        post("api/space") {
            Space.processPayload(
                KtorRequestAdapter(call),
                ktorClient,
                AppInstanceStorage
            ) { payload ->
                when (payload) {
                    is InitPayload -> {
                        requestPermissions(payload.clientId)
                        // If initialization is successful,
                        // respond with HTTP 200 OK
                        SpaceHttpResponse.RespondWithOk
                    }

                    is ListCommandsPayload -> {
                        call.respondText(
                            // JSON serializer
                            ObjectMapper().writeValueAsString(getSupportedCommands()),
                            ContentType.Application.Json
                        )

                        SpaceHttpResponse.RespondWithOk
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

                        SpaceHttpResponse.RespondWithOk
                    }

                    else -> {
                        SpaceHttpResponse.RespondWithCode(401)
                    }
                }
            }
        }
    }
}

//        post("api/space/message") {
//            val body = call.receiveText()
//            when (val payload = readPayload(body)) {
//                is ListCommandsPayload -> {
//                    // Space requests the list of supported commands
//                    call.respondText(
//                        // JSON serializer
//                        ObjectMapper().writeValueAsString(getSupportedCommands()),
//                        ContentType.Application.Json
//                    )
//                }
//
//                is MessagePayload -> {
//                    // user sent a message to the application
//                    val commandName = payload.command()
//                    val command = supportedCommands.find { it.name == commandName }
//                    if (command == null) {
//                        runHelpCommand(payload)
//                    } else {
//                        launch { command.run(payload) }
//                    }
//                    call.respond(HttpStatusCode.OK, "")
//                }
//            }
//        }
//
//        post("api/space/greetings") {
//            val body = call.receiveText()
//
//            transaction {
//                val payload = readPayload(body)
//                val newUser = UserProfile.new {
//                    clientId = payload.clientId!!
//                    spaceUserId = ((payload as WebhookRequestPayload).payload as ProfileOrganizationEvent).member.id
//                    subscribed = false
//                }
//                runBlocking {
//                    sendMessage(newUser.clientId, newUser.spaceUserId, greetNewUser())
//                }
//            }
//        }
//    }
//}

//private fun greetNewUser(): ChatMessage {
//    return message {
//        section {
//            text(
//                "Glad to see you here! Be my guest and write me"
//            )
//        }
//    }
//}