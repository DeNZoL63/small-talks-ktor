package ru.ordertime.small_talks.command

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ordertime.small_talks.client.sendMessage
import ru.ordertime.small_talks.domain.UserProfile
import ru.ordertime.small_talks.domain.UserProfiles
import space.jetbrains.api.runtime.types.ChatMessage


fun changeSubscriptionOnMeetings(userId: String, clientId: String, subscribe: Boolean, messageOnSuccess: ChatMessage) {
    runBlocking {
        transaction {
            var user = UserProfile.find(UserProfiles.spaceUserId eq userId).singleOrNull()
            if (user == null) {
                user = UserProfile.new {}
            }
            user.spaceUserId = userId
            user.clientId = clientId
            user.subscribed = subscribe
        }
        sendMessage(clientId, userId, messageOnSuccess)
    }
}