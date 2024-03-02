package com.example.routes

import com.example.UserSession
import com.example.services.checkLogin
import com.example.services.createUser
import com.example.services.getUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

data class LoginForm(val username: String, val password: String)
data class SignupForm(val username: String, val password: String)
data class SuccessResponse(val success: Boolean, val message: String? = null)

/**
 * Routes for authenticating
 * @author Justin Forseth
 */
fun Application.configureAuthRoutes() {
  routing {
    post("/signin") {
      val loginForm = call.receive<LoginForm>()
      if (checkLogin(loginForm.username, loginForm.password)) {
        call.sessions.set(UserSession(username = loginForm.username))
        call.respond(SuccessResponse(true))
      } else {
        call.response.status(HttpStatusCode(401, "Wrong username or password"))
        call.respond(SuccessResponse(false))
      }
    }

    post("/signup") {
      val signupForm = call.receive<SignupForm>()
      val user = getUser(signupForm.username)
      if (user != null) {
        call.response.status(HttpStatusCode(400, "User already exists"))
        call.respond(SuccessResponse(false, "User already exists"))
      }
      createUser(signupForm.username, signupForm.password)

      call.sessions.set(UserSession(username = signupForm.username))
      call.respond(SuccessResponse(true))
    }

    get("/logout") {
      call.sessions.clear<UserSession>()
      call.respondRedirect("/#/login")
    }
  }
}