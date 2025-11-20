package com.ulissesarredondo.repository

import com.ulissesarredondo.repository.Locks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.sqlite.JDBC"
        // This creates a file named 'data.db' in your project's root folder
        val jdbcURL = "jdbc:sqlite:data.db"

        val database = Database.Companion.connect(jdbcURL, driverClassName)


        transaction(database) {
            SchemaUtils.create(Locks)
        }
    }

    // A helper function to run database queries on a background thread.
    // Ktor is asynchronous, but JDBC (databases) are blocking.
    // This bridge prevents your server from freezing while waiting for the DB.
    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}