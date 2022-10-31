package ru.ordertime.small_talks.command

import kotlinx.coroutines.runBlocking
import ru.ordertime.small_talks.client.sendMessage
import ru.ordertime.small_talks.database.UserProfileRepository
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.types.ApiIcon
import space.jetbrains.api.runtime.types.ChatMessage
import space.jetbrains.api.runtime.types.MessageOutline
import space.jetbrains.api.runtime.types.MessagePayload

suspend fun runAddUserToList(payload: MessagePayload) {
    addUserToList(payload.userId)
}

suspend fun addUserToList(userId: String) {
    val repository = UserProfileRepository()
    runBlocking {
        val founded = repository.userProfile(userId)
        if (founded == null) {
            repository.addNewUserProfile(userId, true)
        } else {
            repository.editUserProfile(
                id = founded.id,
                spaceId = founded.spaceId,
                wantToMeet = true
            )
        }
        sendMessage(userId, acceptNewUser())
    }
}


private fun acceptNewUser(): ChatMessage {
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
