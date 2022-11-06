package ru.ordertime.small_talks.service

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ordertime.small_talks.client.createMeeting
import ru.ordertime.small_talks.domain.UserProfile
import ru.ordertime.small_talks.domain.UserProfiles
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

// TODO: remove test values
val workingTime:IntRange = 0..23

fun initManager() {
    val scheduler = Scheduler {
        planMeetings()
    }
    scheduler.scheduleExecution(Every(1, TimeUnit.HOURS))

}

private fun planMeetings() {
    val now = LocalDateTime.now()
    if (now.hour !in workingTime) {
        return
    }

    val nowLocalDate = now.toLocalDate()
    val startTime = LocalTime.of(now.hour + 1, 0, 0)

    val start = LocalDateTime.of(nowLocalDate, startTime)
    val end = LocalDateTime.of(nowLocalDate, startTime.plusMinutes(15))

    val topic = generateTopic()

    val profiles = transaction {
        UserProfile.find(UserProfiles.subscribed eq true)
            .groupBy({ it.clientId }) { it.spaceUserId }
    }

    profiles.forEach {
        runBlocking {
            createMeeting(
                start = start,
                end = end,
                topic = topic,
                members = it.value
            )
        }
    }
}