package ru.ordertime.small_talks.command

import ru.ordertime.small_talks.client.sendMessage
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.types.*

// command for showing chatbot help
suspend fun runHelpCommand(payload: MessagePayload) {
    // get user ID from the payload and send them a help message
    sendMessage(payload.userId, helpMessage())
}

// build the help message using the special DSL
fun helpMessage(): ChatMessage {
    return message {
        MessageOutline(
            icon = ApiIcon("checkbox-checked"),
            text = "Remind me bot help"
        )
        section {
            text("List of available commands", MessageStyle.PRIMARY)
            fields {
                supportedCommands.forEach {
                    field(it.name, it.info)
                }
            }
        }
    }
}