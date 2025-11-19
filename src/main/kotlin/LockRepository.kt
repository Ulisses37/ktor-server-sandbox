package com.ulissesarredondo

import com.ulissesarredondo.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class LockRepository {
    // Returns the Lock object if found, or null if not
    suspend fun getLock(file: String): Lock? = dbQuery {
        Lock.find { Locks.filePath eq file}
            .singleOrNull()
    }

    // Returns true if lock was created, false if it failed (already locked)
    suspend fun addLock(file: String, userName: String): Boolean = dbQuery {
        val existingLock = Lock.find { Locks.filePath eq file}.singleOrNull()

        if(existingLock != null) {
            return@dbQuery false
        }

        Lock.new {
            this.filePath = file
            this.user = userName
            this.timestamp = System.currentTimeMillis()
        }

        true
    }
}