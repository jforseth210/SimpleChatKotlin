package com.example.routes

import com.example.dao.DatabaseSingleton.dbQuery
import com.example.dao.models.Conversation
import com.example.getCurrentUserOr401
import com.example.services.createConversation
import com.example.services.getSerializedConversations
import com.example.services.getUser
import com.example.services.sendMessage
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


data class MessageForm(val message: String, val conversationId: Int)
data class ConversationForm(val user: String)
data class ConversationCreationResponse(val success: Boolean, val id: Int)

/**
 * Routes for use in the chat application itself
 * @author Justin Forseth
 */
fun Application.configureApplicationRoutes() {
  routing {
    // Static plugin. Try to access `/static/index.html`
    staticFiles("/", File("web/dist")) {
    }
    authenticate("auth-session") {
      get("/conversations") {
        dbQuery {
          val user = getCurrentUserOr401(call) ?: return@dbQuery
          call.respond(getSerializedConversations(user))
        }
      }

    }
    authenticate("auth-session") {
      post("/send-message") {
        dbQuery {
          val messageForm = call.receive<MessageForm>()
          val user = getCurrentUserOr401(call) ?: return@dbQuery
          val conversation = Conversation.findById(messageForm.conversationId) ?: return@dbQuery
          sendMessage(conversation, user, messageForm.message)
          call.respond(SuccessResponse(true))
        }
      }
    }
    authenticate("auth-session") {
      post("/add-conversation") {
        dbQuery {
          val currentUser = getCurrentUserOr401(call) ?: return@dbQuery
          val conversationForm = call.receive<ConversationForm>()
          val otherUser = getUser(conversationForm.user)
          if (otherUser == null) {
            call.respond(404)
            return@dbQuery
          }
          if (currentUser == otherUser) {
            call.respond(400)
            return@dbQuery
          }
          val conversation = createConversation(listOf(currentUser, otherUser))
          call.respond(ConversationCreationResponse(true, conversation.id.value))
        }
      }
    }
  }
}

