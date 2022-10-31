package ru.ordertime.small_talks.command

import space.jetbrains.api.runtime.types.*

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
        "remind",
        "Remind me about something in N seconds, e.g., " +
                "to remind about \"the thing\" in 10 seconds, send 'remind 10 the thing' ",
    ) { payload -> runRemindCommand(payload) }
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