package ru.ordertime.small_talks.command

import space.jetbrains.api.runtime.types.CommandDetail
import space.jetbrains.api.runtime.types.Commands
import space.jetbrains.api.runtime.types.ListCommandsPayload
import space.jetbrains.api.runtime.types.MessagePayload

class ApplicationCommand(
    val name: String,
    val info: String,
    val run: suspend (payload: MessagePayload) -> Unit
) {
    /**
     * [CommandDetail] is returned to Space with info about the command.
     * List of commands is shown to the user.
     */
    fun toSpaceCommand() = CommandDetail(name, info)
}

// list of available commands
val supportedCommands = listOf(
    ApplicationCommand(
        "help",
        "Show this help",
    ) { payload -> runHelpCommand(payload) },

    ApplicationCommand(
        "subscribe",
        "I will add you in the list of users who want to meet someone new in our company"
    ) { payload -> runSubscribeUserOnMeetings(payload) },

    ApplicationCommand(
        "unsubscribe",
        "I will remove you from the list of users who want to meet someone new in our company"
    ) { payload -> runUnsubscribeUserFromMeetings(payload) }
)

/**
 * Response to [ListCommandsPayload].
 * Space will display the returned commands as commands supported by your app.
 */
fun getSupportedCommands() = Commands(
    supportedCommands.map {
        it.toSpaceCommand()
    }
)