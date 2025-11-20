package com.ulissesarredondo.models

import kotlinx.serialization.Serializable

@Serializable
data class LockRequest (
    val filePath: String,
    val user: String
)

@Serializable
data class LockResponse(val filePath: String,
                        val user: String,
                        val timestamp: Long)