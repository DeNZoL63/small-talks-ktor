package ru.ordertime.small_talks.domain

import org.jetbrains.exposed.sql.Table
import java.util.UUID

data class UserProfile(val id: UUID, val spaceId: String, val wantToMeet: Boolean)

object UserProfiles : Table() {
    val id = uuid("id").autoGenerate().uniqueIndex()
    val spaceId = varchar("space_id", 255)
    val wantToMeet = bool("want_to_meet")

    override val primaryKey = PrimaryKey(id)
}