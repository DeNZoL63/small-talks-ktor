package ru.ordertime.small_talks.command

import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.types.ApiIcon
import space.jetbrains.api.runtime.types.ChatMessage
import space.jetbrains.api.runtime.types.MessageOutline
import space.jetbrains.api.runtime.types.MessagePayload

fun runUnsubscribeUserFromMeetings(payload: MessagePayload) {
    changeSubscriptionOnMeetings(payload.userId, payload.clientId, false, notifyUserSuccess())
}

private fun notifyUserSuccess(): ChatMessage {
    return message {
        outline(
            MessageOutline(
                icon = ApiIcon("checkbox-checked"),
                text = "Done!"
            )
        )
        section {
            text(
                "You have been unsubscribed from communication meetings.\n" +
                        "Come back when you change your mind"
            )
        }
    }
}
