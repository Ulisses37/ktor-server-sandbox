package com.ulissesarredondo.plugins // Make sure this matches your folder structure

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    // This acts as the translator between HTTP JSON and your Kotlin Data Classes
    install(ContentNegotiation) {
        json()
    }
}