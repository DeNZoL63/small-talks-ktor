package ru.ordertime.small_talks.client

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ordertime.small_talks.domain.SpaceInstance
import ru.ordertime.small_talks.domain.SpaceInstances
import space.jetbrains.api.runtime.SpaceAppInstance
import space.jetbrains.api.runtime.SpaceAuth
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.RequestAdapter
import space.jetbrains.api.runtime.helpers.SpaceAppInstanceStorage
import space.jetbrains.api.runtime.ktorClientForSpace
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.resources.calendars
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.*
import java.time.ZonedDateTime

object AppInstanceStorage : SpaceAppInstanceStorage {
    override suspend fun loadAppInstance(clientId: String): SpaceAppInstance? {
        return convertToSpaceAppInstance(
            findSpaceInstanceByClientId(clientId)
        )
    }

    override suspend fun saveAppInstance(appInstance: SpaceAppInstance) {
        transaction {
            var spaceInstance = findSpaceInstanceByClientId(appInstance.clientId)
            if (spaceInstance == null) {
                spaceInstance = SpaceInstance.new { }
            }
            spaceInstance.clientId = appInstance.clientId
            spaceInstance.spaceServerUrl = appInstance.spaceServer.serverUrl
            spaceInstance.clientSecret = appInstance.clientSecret
        }
    }

    private fun findSpaceInstanceByClientId(clientId: String): SpaceInstance? {
        return transaction {
            SpaceInstance.find(SpaceInstances.clientId eq clientId).firstOrNull()
        }
    }

    private fun convertToSpaceAppInstance(spaceInstance: SpaceInstance?): SpaceAppInstance? {
        if (spaceInstance == null) {
            return null
        }

        return SpaceAppInstance(
            clientId = spaceInstance.clientId,
            clientSecret = spaceInstance.clientSecret,
            spaceServerUrl = spaceInstance.spaceServerUrl
        )
    }

}

val ktorClient = ktorClientForSpace()

class KtorRequestAdapter(private val call: ApplicationCall) : RequestAdapter {
    override suspend fun receiveText() = call.receiveText()

    override fun getHeader(headerName: String) = call.request.headers[headerName]

    override suspend fun respond(httpStatusCode: Int, body: String) {
        call.respond(HttpStatusCode.fromValue(httpStatusCode), body)
    }
}

suspend fun requestPermissions(clientId: String) {
    val spaceClient = getSpaceClient(clientId)
    spaceClient?.applications?.authorizations?.authorizedRights?.requestRights(
        application = ApplicationIdentifier.Me,
        contextIdentifier = GlobalPermissionContextIdentifier,
        rightCodes = listOf(
            "Profile.Memberships.View",
            "Channel.PostMessages",
            "Meeting.Edit",
            "Meeting.View",
            "Profile.View"
        )
    )
}

suspend fun sendMessage(clientId: String, userId: String, message: ChatMessage) {
    val spaceClient = getSpaceClient(clientId)
    spaceClient?.chats?.messages?.sendMessage(
        channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(userId)),
        content = message
    )
}

suspend fun createMeeting(
    clientId: String,
    start: ZonedDateTime,
    end: ZonedDateTime,
    topic: String,
    members: List<String>
) {
    val spaceClient = getSpaceClient(clientId)
    spaceClient?.calendars?.meetings?.createMeeting(
        summary = topic,
        description = "This meeting is created to let you know colleagues better",
        profiles = members,
        occurrenceRule = CalendarEventSpec(
            start = Instant.fromEpochSeconds(start.toEpochSecond()),
            end = Instant.fromEpochSeconds(end.toEpochSecond()),
            recurrenceRule = null,
            allDay = false,
            timezone = ATimeZone(
                id = TimeZone.currentSystemDefault().id
            ),
            parentId = null,
            initialMeetingStart = null,
            busyStatus = BusyStatus.Free,
            nextChainId = null
        )
    )
}

suspend fun getSpaceClient(clientId: String): SpaceClient? {
    val appInstance = AppInstanceStorage.loadAppInstance(clientId)
    if (appInstance == null) {
        return null
    }
    return SpaceClient(
        ktorClient, appInstance,
        SpaceAuth.ClientCredentials()
    )
}

/*
// describes connection to a Space instance
val spaceAppInstance = SpaceAppInstance(
    // Copy-paste the client-id, and the client-secret
    // your app got from Space.
    clientId = "dc42aaad-fed9-4cd9-945e-6f522f6d62e7",
    clientSecret = "4f469fc4c933f4f8dd642a4d69d134a7af7366ca93789e8e47ea9ef972ae96db",
    // URL of your Space instance
    spaceServerUrl = "https://gift.jetbrains.space"
)

private val spaceHttpClient = ktorClientForSpace()

// The Space client is used to call Space API methods.
// The application uses the Client Credentials OAuth flow (see [SpaceAuth.ClientCredentials])
// to authorize on behalf of itself.
val spaceClient =
    SpaceClient(
        ktorClient = spaceHttpClient, appInstance = spaceAppInstance,
        auth = SpaceAuth.ClientCredentials()
    )

// Get user by ID and send 'message' to the user.
// 'spaceClient' gives you access to any Space endpoint.
suspend fun sendMessage(userId: String, message: ChatMessage) {
    spaceClient.chats.messages.sendMessage(
        channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(userId)),
        content = message
    )
}

suspend fun createMeeting(start: LocalDateTime, end: LocalDateTime, topic: String, members: List<String>) {
    spaceClient.calendars.meetings.createMeeting(
        summary = topic,
        description = "This meeting is created to let you know colleagues better",
        profiles = members,
        occurrenceRule = CalendarEventSpec(
            start = Instant.fromEpochSeconds(start.toEpochSecond(ZoneOffset.UTC)),
            end = Instant.fromEpochSeconds(end.toEpochSecond(ZoneOffset.UTC)),
            recurrenceRule = null,
            allDay = false,
            timezone = ATimeZone(
                id = TimeZone.UTC.id
            ),
            parentId = null,
            initialMeetingStart = null,
            busyStatus = BusyStatus.Free,
            nextChainId = null
        )
    )
} */