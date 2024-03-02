package com.example.plugins

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Set up serialization
 * @author Justin Forseth
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            }
    }
}
