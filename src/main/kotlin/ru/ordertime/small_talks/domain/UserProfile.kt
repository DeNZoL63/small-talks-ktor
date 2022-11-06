package ru.ordertime.small_talks.domain

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

class UserProfile(id: EntityID<UUID>): Entity<UUID>(id) {
    companion object : EntityClass<UUID, UserProfile>(UserProfiles)

    var clientId by UserProfiles.clientId
    var spaceUserId by UserProfiles.spaceUserId
    var subscribed by UserProfiles.subscribed
}


object UserProfiles : UUIDTable() {
    val clientId = varchar("client_id", 255)
    val subscribed = bool("subscribed")
    val spaceUserId = varchar("space_user_id", 255)
}