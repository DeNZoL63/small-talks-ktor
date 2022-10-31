package ru.ordertime.small_talks.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.ordertime.small_talks.database.DatabaseFactory.dbQuery
import ru.ordertime.small_talks.domain.UserProfile
import ru.ordertime.small_talks.domain.UserProfiles
import java.util.*

class UserProfileRepository : DAOFacade {

    private fun resultRowToUserProfile(row: ResultRow) = UserProfile(
        id = row[UserProfiles.id],
        spaceId = row[UserProfiles.spaceId],
        wantToMeet = row[UserProfiles.wantToMeet]
    )

    override suspend fun allUserProfiles(): List<UserProfile> = dbQuery {
        UserProfiles
            .selectAll()
            .map(::resultRowToUserProfile)
    }

    override suspend fun userProfile(id: UUID): UserProfile? = dbQuery {
        UserProfiles
            .select(UserProfiles.id eq id)
            .map(::resultRowToUserProfile)
            .singleOrNull()
    }

    override suspend fun userProfile(spaceId: String): UserProfile? = dbQuery {
        UserProfiles
            .select(UserProfiles.spaceId eq spaceId)
            .map(::resultRowToUserProfile)
            .singleOrNull()
    }

    override suspend fun addNewUserProfile(spaceId: String, wantToMeet: Boolean): UserProfile? = dbQuery {
        val insertStatement = UserProfiles
            .insert {
                it[UserProfiles.spaceId] = spaceId
                it[UserProfiles.wantToMeet] = wantToMeet
            }

        insertStatement.resultedValues
            ?.singleOrNull()
            ?.let(::resultRowToUserProfile)
    }

    override suspend fun editUserProfile(id: UUID, spaceId: String, wantToMeet: Boolean): Boolean = dbQuery {
        UserProfiles.update({ UserProfiles.id eq id }) {
            it[UserProfiles.spaceId] = spaceId
            it[UserProfiles.wantToMeet] = wantToMeet
        } > 0
    }

    override suspend fun deleteUserProfile(id: UUID): Boolean = dbQuery {
        UserProfiles.deleteWhere { UserProfiles.id eq id } > 0
    }
}