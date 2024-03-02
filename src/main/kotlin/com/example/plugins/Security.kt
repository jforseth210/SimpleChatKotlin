package com.example

import com.example.dao.DatabaseSingleton.dbQuery
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.mindrot.jbcrypt.BCrypt

data class UserSession(val username: String) : Principal
data class LoginForm(val username: String, val password: String)
data class SignupForm(val username: String, val password: String)
data class SuccessResponse(val success: Boolean, val message: String?= null)

fun Application.configureSecurity() {
  install(Sessions) {
    cookie<UserSession>("user_session") {
      cookie.path = "/"
      cookie.maxAgeInSeconds = 30
    }
  }
  install(Authentication) {
      session<UserSession>("auth-session") {
      validate { session ->
        if(true){session}else{null}
      }
      challenge {
        call.respond(HttpStatusCode(401,""))
      }
    }
  }

  routing {
    post("/signin") {
        val loginForm = call.receive<LoginForm>()
        dbQuery {
          val user = User.find { Users.username eq loginForm.username }.firstOrNull()

          if(user==null){
            call.response.status(HttpStatusCode(401, "User doesn't exist"))
            call.respond(SuccessResponse(false))
            return@dbQuery
          }
          if(BCrypt.checkpw(loginForm.password,user.hashedPassword)){
              call.sessions.set(UserSession(username = loginForm.username))
              call.respond(SuccessResponse(true))
          } else {
            call.response.status(HttpStatusCode(401, "Wrong password"))
            call.respond(SuccessResponse(false))
          }
        }
    }
    post("/signup") {
      val signupForm = call.receive<SignupForm>()
      dbQuery{
        val user = User.find {Users.username eq signupForm.username}.firstOrNull()
        if (user != null) {
          call.response.status(HttpStatusCode(400, "User already exists"))
          call.respond(SuccessResponse(false, "User already exists"))
        }
        User.new{
          username=signupForm.username
          hashedPassword=BCrypt.hashpw(signupForm.password, BCrypt.gensalt(12))
        }
      }
      call.sessions.set(UserSession(username = signupForm.username))
      call.respond(SuccessResponse(true))
    }
    get("/logout") {
      call.sessions.clear<UserSession>()
      call.respondRedirect("/#/login")
    }
  }
}