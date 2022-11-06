package ru.ordertime.small_talks.command

import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.types.ApiIcon
import space.jetbrains.api.runtime.types.ChatMessage
import space.jetbrains.api.runtime.types.MessageOutline
import space.jetbrains.api.runtime.types.MessagePayload

fun runSubscribeUserOnMeetings(payload: MessagePayload) {
    changeSubscriptionOnMeetings(payload.userId, payload.clientId, true, notifyUserSuccess())
}

private fun notifyUserSuccess(): ChatMessage {
    return message {
        outline(
            MessageOutline(
                icon = ApiIcon("checkbox-checked"),
                text = "Great! You are in the game now."
            )
        )
        section {
            text(
                "Wait for some meetings! I'll create a meeting for you once a day.\n" +
                        "And will try to find the new companion."
            )
        }
    }
}
