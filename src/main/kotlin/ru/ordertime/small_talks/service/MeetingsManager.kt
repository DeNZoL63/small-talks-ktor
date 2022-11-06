package ru.ordertime.small_talks.service

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ordertime.small_talks.client.createMeeting
import ru.ordertime.small_talks.domain.UserProfile
import ru.ordertime.small_talks.domain.UserProfiles
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

val workingTime: IntRange = 9..18

fun initManager() {
    val scheduler = Scheduler {
        try {
            planMeetings()
        } catch (e: Exception) {
        }
    }
    scheduler.scheduleExecution(Every(1, TimeUnit.MINUTES))

}

private fun planMeetings() {
    val now = LocalDateTime.now()
    if (now.hour !in workingTime) {
        return
    }

    var nowLocalDate = now.toLocalDate()
    val nextHour: Int = if (now.hour == 23) {
        nowLocalDate = nowLocalDate.plusDays(1)
        0
    } else now.hour + 1
    val startTime = LocalTime.of(nextHour, 0, 0)

    val start = LocalDateTime.of(nowLocalDate, startTime)
        .atZone(ZoneId.systemDefault())
    val end = LocalDateTime.of(nowLocalDate, startTime.plusMinutes(15))
        .atZone(ZoneId.systemDefault())

    val topic = generateTopic()

    val profiles = transaction {
        UserProfile.find(UserProfiles.subscribed eq true)
            .groupBy({ it.clientId }) { it.spaceUserId }
    }

    profiles.forEach {
        runBlocking {
            createMeeting(
                clientId = it.key,
                start = start,
                end = end,
                topic = topic,
                members = it.value
            )
        }
    }
}