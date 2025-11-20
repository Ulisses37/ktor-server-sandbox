package com.ulissesarredondo.repository

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.ulissesarredondo.models.*

class LockRepositoryTest {

    // Run this before EVERY test to give us a clean slate
    @BeforeEach
    fun setup(){
        Database.Companion.connect(url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

        transaction {
            SchemaUtils.drop(Locks)

            SchemaUtils.create(Locks)
        }
    } // fun setup()

    @Test
    fun `addLock - Lock Creation - Return True on Lock Creation`() = runBlocking {
        val repository = LockRepository()
        val result = repository.addLock("test_file.java", "John Doe")

        Assertions.assertTrue(result, "Should return true when adding a new lock")

        val lock = repository.getLock("test_file.java")
        Assertions.assertNotNull(lock)
        Assertions.assertEquals("John Doe", lock?.user)
    }

    @Test
    fun `addLock - Lock Rejection When Lock Already Exists - Return false on Lock Creation`() = runBlocking {
        val repository = LockRepository()
        repository.addLock("test_file.java", "User A")

        val result = repository.addLock("test_file.java", "User B")

        Assertions.assertFalse(result, "Should return false because User A already locked it")

        val lock = repository.getLock("test_file.java")
        Assertions.assertEquals("User A", lock?.user)
    }

}