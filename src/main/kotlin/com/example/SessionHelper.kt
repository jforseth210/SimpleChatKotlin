package com.example

import User
import com.example.services.getUser
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

/**
 * Helper function to get the current user or send a 401 response
 * @author Justin Forseth
 */
suspend fun getCurrentUserOr401(call: ApplicationCall): User? {
  val userSession = call.sessions.get<UserSession>()
  val username: String = userSession?.username ?: run {
    call.respond(401)
    return null
  }
  val user = getUser(username) ?: run {
    call.respond(401)
    return null
  }
  return user
}
