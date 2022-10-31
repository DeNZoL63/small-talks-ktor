package ru.ordertime.small_talks.client

import space.jetbrains.api.runtime.SpaceAppInstance
import space.jetbrains.api.runtime.SpaceAuth
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.ktorClientForSpace
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.ChannelIdentifier
import space.jetbrains.api.runtime.types.ChatMessage
import space.jetbrains.api.runtime.types.ProfileIdentifier

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
    SpaceClient(ktorClient = spaceHttpClient, appInstance = spaceAppInstance,
        auth = SpaceAuth.ClientCredentials())

// Get user by ID and send 'message' to the user.
// 'spaceClient' gives you access to any Space endpoint.
suspend fun sendMessage(userId: String, message: ChatMessage) {
    spaceClient.chats.messages.sendMessage(
        channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(userId)),
        content = message
    )
}