package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

/**
 * Set up security
 * @author Justin Forseth
 */
data class UserSession(val username: String) : Principal

fun Application.configureSecurity() {
  install(Sessions) {
    cookie<UserSession>("user_session") {
      cookie.path = "/"
      cookie.maxAgeInSeconds = 3600
    }
  }
  install(Authentication) {
    session<UserSession>("auth-session") {
      validate { session ->
        if (true) {
          session
        } else {
          null
        }
      }
      challenge {
        call.respond(HttpStatusCode(401, ""))
      }
    }
  }
}