package com.ulissesarredondo.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

import com.ulissesarredondo.models.*
import com.ulissesarredondo.repository.LockRepository

// A simple DTO (Data Transfer Object) for sending lock data back to the client
@Serializable
data class LockResponse(val filePath: String, val user: String, val timestamp: Long)

fun Application.configureRouting() {
    // Initialize the repository
    val repository = LockRepository()

    routing {
        route("/lock") {
            // 1. Request a Lock (The Check-Out)
            post {
                // We wrap this in a try-catch block in case the JSON is malformed
                try {
                    val request = call.receive<LockRequest>()

                    // Try to add the lock to the database
                    val success = repository.addLock(request.filePath, request.user)

                    if (success) {
                        call.respond(HttpStatusCode.Created, "Lock granted")
                    } else {
                        // Per your State Machine: Active lock exists -> Conflict (409)
                        call.respond(HttpStatusCode.Conflict, "File is already locked")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.BadRequest, "Invalid Request Format")
                }
            }

            // 2. Check Lock Status
            // Usage: GET /lock?filePath=test_project/utils.java
            get {
                val filePath = call.request.queryParameters["filePath"]

                if (filePath == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'filePath' query parameter")
                    return@get
                }

                val lock = repository.getLock(filePath)

                if (lock != null) {
                    // Convert the Database Entity to a Serializable DTO
                    val response = LockResponse(lock.filePath, lock.user, lock.timestamp)
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.NotFound, "No active lock found")
                }
            }
        }
    }
}