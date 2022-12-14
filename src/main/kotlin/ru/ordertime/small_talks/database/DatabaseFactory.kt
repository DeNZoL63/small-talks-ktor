package ru.ordertime.small_talks.database

import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ordertime.small_talks.domain.SpaceInstances
import ru.ordertime.small_talks.domain.UserProfiles

object DatabaseFactory {
    fun init() {
        val config = ConfigFactory.load()
        val datasourceUrl = config.getString("ktor.datasource.url")
        val user = config.getString("ktor.datasource.user")
        val password = config.getString("ktor.datasource.password")
        val driverClassName = "org.postgresql.Driver"
        val database = Database.connect(
            url = datasourceUrl,
            driver = driverClassName,
            user = user,
            password = password
        )

        transaction(database) {
            SchemaUtils.create(UserProfiles)
            SchemaUtils.create(SpaceInstances)
        }
    }
}