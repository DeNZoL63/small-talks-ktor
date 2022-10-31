package ru.ordertime.small_talks.database

import ru.ordertime.small_talks.domain.UserProfile
import java.util.UUID

interface DAOFacade {
    suspend fun allUserProfiles(): List<UserProfile>
    suspend fun userProfile(id: UUID): UserProfile?
    suspend fun userProfile(spaceId: String): UserProfile?
    suspend fun addNewUserProfile(spaceId: String, wantToMeet: Boolean): UserProfile?
    suspend fun editUserProfile(id: UUID, spaceId: String, wantToMeet: Boolean): Boolean
    suspend fun deleteUserProfile(id: UUID): Boolean
}