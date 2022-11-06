package ru.ordertime.small_talks.domain

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import space.jetbrains.api.runtime.SpaceAppInstance
import java.util.*

class SpaceInstance(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, SpaceInstance>(SpaceInstances)

    var clientId by SpaceInstances.clientId
    var clientSecret by SpaceInstances.clientSecret
    var spaceServerUrl by SpaceInstances.spaceServerUrl
}

object SpaceInstances : UUIDTable() {
    val clientId = varchar("client_id", 255).uniqueIndex()
    val clientSecret = varchar("client_secret", 255)
    val spaceServerUrl = varchar("space_server_url", 255)
}