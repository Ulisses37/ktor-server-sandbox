package com.ulissesarredondo

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// 1. The Table Definition (Singleton Object)
// We extend IntIdTable because it automatically gives us an auto-incrementing integer ID.
object Locks : IntIdTable() {
    val filePath = varchar("file_path", 255).uniqueIndex() // Ensure file paths are unique
    val user = varchar("user", 50)
    val timestamp = long("timestamp")
}

// 2. The Entity Definition (Class)
// This represents a SINGLE row in the table.
class Lock(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Lock>(Locks)

    var filePath by Locks.filePath
    var user by Locks.user
    var timestamp by Locks.timestamp
}

